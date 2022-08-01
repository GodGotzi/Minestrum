package net.md_5.bungee;

import at.gotzi.api.template.logging.GLevel;
import at.gotzi.minestrum.Minestrum;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.ResourceLeakDetector;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.ScoreComponentSerializer;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;
import net.md_5.bungee.command.CommandBungee;
import net.md_5.bungee.command.CommandEnd;
import net.md_5.bungee.command.CommandIP;
import net.md_5.bungee.command.CommandPerms;
import net.md_5.bungee.command.CommandReload;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.forge.ForgeConstants;
import net.md_5.bungee.module.ModuleManager;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.query.RemoteQuery;
import net.md_5.bungee.util.scheduler.BungeeScheduler;
import net.md_5.bungee.util.CaseInsensitiveMap;

/**
 * Main BungeeCord proxy class.
 */
public class Bungee extends ProxyServer {

    /**
     * Current operation state.
     */
    public volatile boolean isRunning;
    /**
     * Configuration.
     */
    @Getter
    public final Configuration config = new Configuration();
    /**
     * Localization bundle.
     */
    private ResourceBundle baseBundle;
    private ResourceBundle customBundle;
    public EventLoopGroup eventLoops;
    /**
     * locations.yml save thread.
     */
    private final Timer saveThread = new Timer( "Reconnect Saver" );
    private final Timer metricsThread = new Timer( "Metrics Thread" );
    /**
     * Server socket listener.
     */
    private final Collection<Channel> listeners = new HashSet<>();
    /**
     * Fully qualified connections.
     */
    private final Map<String, UserConnection> connections = new CaseInsensitiveMap<>();
    // Used to help with packet rewriting
    private final Map<UUID, UserConnection> connectionsByOfflineUUID = new HashMap<>();
    private final Map<UUID, UserConnection> connectionsByUUID = new HashMap<>();
    private final ReadWriteLock connectionLock = new ReentrantReadWriteLock();
    /**
     * Lock to protect the shutdown process from being triggered simultaneously
     * from multiple sources.
     */
    private final ReentrantLock shutdownLock = new ReentrantLock();
    /**
     * Plugin manager.
     */
    @Getter
    public final PluginManager pluginManager;
    @Getter
    @Setter
    private ReconnectHandler reconnectHandler;
    @Getter
    @Setter
    private ConfigurationAdapter configurationAdapter = new YamlConfig();
    private final Collection<String> pluginChannels = new HashSet<>();
    @Getter
    private final File pluginsFolder = new BungeeFile( "plugins" );
    @Getter
    private final BungeeScheduler scheduler = new BungeeScheduler();
    @Getter
    private final Logger logger;
    @Getter
    private final Properties properties;

    public final Gson gson = new GsonBuilder()
            .registerTypeAdapter( BaseComponent.class, new ComponentSerializer() )
            .registerTypeAdapter( TextComponent.class, new TextComponentSerializer() )
            .registerTypeAdapter( TranslatableComponent.class, new TranslatableComponentSerializer() )
            .registerTypeAdapter( KeybindComponent.class, new KeybindComponentSerializer() )
            .registerTypeAdapter( ScoreComponent.class, new ScoreComponentSerializer() )
            .registerTypeAdapter( SelectorComponent.class, new SelectorComponentSerializer() )
            .registerTypeAdapter( ServerPing.PlayerInfo.class, new PlayerInfoSerializer() )
            .registerTypeAdapter( Favicon.class, Favicon.getFaviconTypeAdapter() ).create();
    @Getter
    private ConnectionThrottle connectionThrottle;
    private final ModuleManager moduleManager = new ModuleManager();

    {
        // TODO: Proper fallback when we interface the manager
        registerChannel( "BungeeCord" );
    }

    public static Bungee getInstance()
    {
        return (Bungee) ProxyServer.getInstance();
    }

    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public Bungee(Logger logger, Properties properties) throws IOException {
        setInstance(this);
        this.logger = logger;
        this.properties = properties;

        this.config.load();
        // Java uses ! to indicate a resource inside of a jar/zip/other container. Running Bungee from within a directory that has a ! will cause this to muck up.
        Preconditions.checkState( new BungeeFile( "." ).getAbsolutePath().indexOf( '!' ) == -1, "Cannot use BungeeCord in directory with ! in path." );

        try
        {
            baseBundle = ResourceBundle.getBundle( "messages" );
        } catch ( MissingResourceException ex )
        {
            baseBundle = ResourceBundle.getBundle( "messages", Locale.ENGLISH );
        }
        reloadMessages();

        Minestrum.getInstance().getLogger().log(GLevel.Debug, "test");

        pluginManager = new PluginManager( this );
    }

    /**
     * Start this proxy instance by loading the configuration, plugins and
     * starting the connect thread.
     *
     * @throws Exception any critical errors encountered
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    public void start() throws Exception
    {
        System.setProperty( "io.netty.selectorAutoRebuildThreshold", "0" ); // Seems to cause Bungee to stop accepting connections
        if ( System.getProperty( "io.netty.leakDetectionLevel" ) == null && System.getProperty( "io.netty.leakDetection.level" ) == null )
        {
            ResourceLeakDetector.setLevel( ResourceLeakDetector.Level.DISABLED ); // Eats performance
        }

        eventLoops = PipelineUtils.newEventLoopGroup( 0, new ThreadFactoryBuilder().setNameFormat( "Netty IO Thread #%1$d" ).build() );

        isRunning = true;

        if ( config.getThrottle() > 0 )
        {
            connectionThrottle = new ConnectionThrottle( config.getThrottle(), config.getThrottleLimit() );
        }
        startListeners();

        saveThread.scheduleAtFixedRate( new TimerTask()
        {
            @Override
            public void run()
            {
                if ( getReconnectHandler() != null )
                {
                    getReconnectHandler().save();
                }
            }
        }, 0, TimeUnit.MINUTES.toMillis( 5 ) );
        metricsThread.scheduleAtFixedRate( new Metrics(), 0, TimeUnit.MINUTES.toMillis( Metrics.PING_INTERVAL ) );

        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                independentThreadStop( getTranslation( "restart" ), false );
            }
        } );
    }

    public void startListeners() {
        for ( final ListenerInfo info : config.getListeners() ) {

            if ( info.isProxyProtocol() )
            {
                getLogger().log( Level.WARNING, "Using PROXY protocol for listener {0}, please ensure this listener is adequately firewalled.", info.getSocketAddress() );

                if ( connectionThrottle != null )
                {
                    connectionThrottle = null;
                    getLogger().log( Level.WARNING, "Since PROXY protocol is in use, internal connection throttle has been disabled." );
                }
            }

            ChannelFutureListener listener = future -> {
                if ( future.isSuccess() )
                {
                    listeners.add( future.channel() );
                    getLogger().log( Level.INFO, "Listening on {0}", info.getSocketAddress());
                } else
                {
                    getLogger().log( Level.WARNING, "Could not bind to host " + info.getSocketAddress(), future.cause() );
                }
            };

            new ServerBootstrap()
                    .channel( PipelineUtils.getServerChannel( info.getSocketAddress() ) )
                    .option( ChannelOption.SO_REUSEADDR, true ) // TODO: Move this elsewhere!
                    .childAttr( PipelineUtils.LISTENER, info )
                    .childHandler( PipelineUtils.SERVER_CHILD )
                    .group( eventLoops )
                    .localAddress( info.getSocketAddress() )
                    .bind().addListener( listener );

            if ( info.isQueryEnabled() ) {
                Preconditions.checkArgument( info.getSocketAddress() instanceof InetSocketAddress, "Can only create query listener on UDP address" );

                ChannelFutureListener bindListener = future -> {
                    if ( future.isSuccess() )
                    {
                        listeners.add( future.channel() );
                        getLogger().log( Level.INFO, "Started query on " + future.channel().localAddress() );
                    } else
                    {
                        getLogger().log( Level.WARNING, "Could not bind to host " + info.getSocketAddress(), future.cause() );
                    }
                };
                new RemoteQuery( this, info ).start( PipelineUtils.getDatagramChannel(), new InetSocketAddress( info.getHost().getAddress(), info.getQueryPort() ), eventLoops, bindListener );
            }
        }
    }

    public void stopListeners()
    {
        for ( Channel listener : listeners )
        {
            getLogger().log( Level.INFO, "Closing listener {0}", listener );
            try
            {
                listener.close().syncUninterruptibly();
            } catch ( ChannelException ex )
            {
                getLogger().severe( "Could not close listen thread" );
            }
        }
        listeners.clear();
    }

    @Override
    public void stop()
    {
        stop( getTranslation( "restart" ) );
    }

    @Override
    public void stop(final String reason) {
        independentThreadStop( reason, true );
    }

    // This must be run on a separate thread to avoid deadlock!
    @SuppressFBWarnings("DM_EXIT")
    @SuppressWarnings("TooBroadCatch")
    private void independentThreadStop(final String reason, boolean callSystemExit)
    {
        // Acquire the shutdown lock
        // This needs to actually block here, otherwise running 'end' and then ctrl+c will cause the thread to terminate prematurely
        shutdownLock.lock();

        // Acquired the shutdown lock
        if ( !isRunning )
        {
            // Server is already shutting down - nothing to do
            shutdownLock.unlock();
            return;
        }
        isRunning = false;

        stopListeners();
        getLogger().info( "Closing pending connections" );

        connectionLock.readLock().lock();
        try
        {
            getLogger().log( Level.INFO, "Disconnecting {0} connections", connections.size() );
            for ( UserConnection user : connections.values() )
            {
                user.disconnect( reason );
            }
        } finally
        {
            connectionLock.readLock().unlock();
        }

        try
        {
            Thread.sleep( 500 );
        } catch ( InterruptedException ignored)
        {
        }

        if ( reconnectHandler != null )
        {
            getLogger().info( "Saving reconnect locations" );
            reconnectHandler.save();
            reconnectHandler.close();
        }
        saveThread.cancel();
        metricsThread.cancel();

        getLogger().info( "Closing IO threads" );
        eventLoops.shutdownGracefully();
        try
        {
            eventLoops.awaitTermination( Long.MAX_VALUE, TimeUnit.NANOSECONDS );
        } catch ( InterruptedException ignored)
        {
        }

        shutdownLock.unlock();
    }

    /**
     * Broadcasts a packet to all clients that is connected to this instance.
     *
     * @param packet the packet to send
     */
    public void broadcast(DefinedPacket packet)
    {
        connectionLock.readLock().lock();
        try
        {
            for ( UserConnection con : connections.values() )
            {
                con.unsafe().sendPacket( packet );
            }
        } finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public String getName()
    {
        return "BungeeCord";
    }

    @Override
    public String getVersion()
    {
        return ( Bungee.class.getPackage().getImplementationVersion() == null ) ? "unknown" : Bungee.class.getPackage().getImplementationVersion();
    }

    public void reloadMessages()
    {
        File file = new BungeeFile( "messages.properties" );
        if ( file.isFile() )
        {
            try ( FileReader rd = new FileReader( file ) )
            {
                customBundle = new PropertyResourceBundle( rd );
            } catch ( IOException ex )
            {
                getLogger().log( Level.SEVERE, "Could not load custom messages.properties", ex );
            }
        }
    }

    @Override
    public String getTranslation(String name, Object... args) {
        String translation = "<translation '" + name + "' missing>";
        try {
            translation = MessageFormat.format( customBundle != null && customBundle.containsKey( name ) ? customBundle.getString( name ) : baseBundle.getString( name ), args );
        } catch ( MissingResourceException ignored) {
        }
        return translation;
    }

    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        connectionLock.readLock().lock();
        try
        {
            return Collections.unmodifiableCollection( new HashSet<>( connections.values() ) );
        } finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public int getOnlineCount()
    {
        return connections.size();
    }

    @Override
    public ProxiedPlayer getPlayer(String name)
    {
        connectionLock.readLock().lock();
        try
        {
            return connections.get( name );
        } finally
        {
            connectionLock.readLock().unlock();
        }
    }

    public UserConnection getPlayerByOfflineUUID(UUID name)
    {
        connectionLock.readLock().lock();
        try
        {
            return connectionsByOfflineUUID.get( name );
        } finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public ProxiedPlayer getPlayer(UUID uuid)
    {
        connectionLock.readLock().lock();
        try
        {
            return connectionsByUUID.get( uuid );
        } finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public Map<String, ServerInfo> getServers()
    {
        return config.getServers();
    }

    @Override
    public ServerInfo getServerInfo(String name)
    {
        return getServers().get( name );
    }

    @Override
    @Synchronized("pluginChannels")
    public void registerChannel(String channel)
    {
        pluginChannels.add( channel );
    }

    @Override
    @Synchronized("pluginChannels")
    public void unregisterChannel(String channel)
    {
        pluginChannels.remove( channel );
    }

    @Override
    @Synchronized("pluginChannels")
    public Collection<String> getChannels()
    {
        return Collections.unmodifiableCollection( pluginChannels );
    }

    public PluginMessage registerChannels(int protocolVersion)
    {
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_13 )
        {
            return new PluginMessage( "minecraft:register", Util.format( Iterables.transform( pluginChannels, PluginMessage.MODERNISE ), "\00" ).getBytes( Charsets.UTF_8 ), false );
        }

        return new PluginMessage( "REGISTER", Util.format( pluginChannels, "\00" ).getBytes( Charsets.UTF_8 ), false );
    }

    @Override
    public int getProtocolVersion()
    {
        return ProtocolConstants.SUPPORTED_VERSION_IDS.get( ProtocolConstants.SUPPORTED_VERSION_IDS.size() - 1 );
    }

    @Override
    public String getGameVersion()
    {
        return ProtocolConstants.SUPPORTED_VERSIONS.get( 0 ) + "-" + ProtocolConstants.SUPPORTED_VERSIONS.get( ProtocolConstants.SUPPORTED_VERSIONS.size() - 1 );
    }

    @Override
    public ServerInfo constructServerInfo(String name, InetSocketAddress address, String motd, boolean restricted)
    {
        return constructServerInfo( name, (SocketAddress) address, motd, restricted );
    }

    @Override
    public ServerInfo constructServerInfo(String name, SocketAddress address, String motd, boolean restricted)
    {
        return new BungeeServerInfo( name, address, motd, restricted );
    }

    @Override
    @Deprecated
    public CommandSender getConsole() {
        return null;
    }

    @Override
    public void broadcast(String message)
    {
        broadcast( TextComponent.fromLegacyText( message ) );
    }

    @Override
    public void broadcast(BaseComponent... message) {
        getLogger().log(GLevel.Info, BaseComponent.toLegacyText( message ));
        for ( ProxiedPlayer player : getPlayers() )
        {
            player.sendMessage( message );
        }
    }

    @Override
    public void broadcast(BaseComponent message)
    {
        getLogger().log(GLevel.Info, BaseComponent.toLegacyText( message ));
        for ( ProxiedPlayer player : getPlayers() ) {
            player.sendMessage( message );
        }
    }

    public void addConnection(UserConnection con)
    {
        connectionLock.writeLock().lock();
        try
        {
            connections.put( con.getName(), con );
            connectionsByUUID.put( con.getUniqueId(), con );
            connectionsByOfflineUUID.put( con.getPendingConnection().getOfflineId(), con );
        } finally
        {
            connectionLock.writeLock().unlock();
        }
    }

    public void removeConnection(UserConnection con)
    {
        connectionLock.writeLock().lock();
        try
        {
            // TODO See #1218
            if ( connections.get( con.getName() ) == con )
            {
                connections.remove( con.getName() );
                connectionsByUUID.remove( con.getUniqueId() );
                connectionsByOfflineUUID.remove( con.getPendingConnection().getOfflineId() );
            }
        } finally
        {
            connectionLock.writeLock().unlock();
        }
    }

    @Override
    public Collection<String> getDisabledCommands()
    {
        return config.getDisabledCommands();
    }

    @Override
    public Collection<ProxiedPlayer> matchPlayer(final String partialName)
    {
        Preconditions.checkNotNull( partialName, "partialName" );

        ProxiedPlayer exactMatch = getPlayer( partialName );
        if ( exactMatch != null )
        {
            return Collections.singleton( exactMatch );
        }

        return Sets.newHashSet( Iterables.filter( getPlayers(), new Predicate<ProxiedPlayer>()
        {

            @Override
            public boolean apply(ProxiedPlayer input)
            {
                return ( input == null ) ? false : input.getName().toLowerCase( Locale.ROOT ).startsWith( partialName.toLowerCase( Locale.ROOT ) );
            }
        } ) );
    }

    @Override
    public Title createTitle()
    {
        return new BungeeTitle();
    }
}
