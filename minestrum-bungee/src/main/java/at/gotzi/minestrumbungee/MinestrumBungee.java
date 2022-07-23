package at.gotzi.minestrumbungee;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.util.ResourceLeakDetector;
import jline.console.ConsoleReader;
import net.md_5.bungee.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.chat.*;
import net.md_5.bungee.command.*;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.log.LoggingOutputStream;
import net.md_5.bungee.module.ModuleManager;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.query.RemoteQuery;
import net.md_5.bungee.scheduler.BungeeScheduler;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.impl.JDK14LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MinestrumBungee extends ProxyServer {
    public volatile boolean isRunning;
    public final Configuration config = new Configuration();
    private ResourceBundle baseBundle;
    private ResourceBundle customBundle;
    public EventLoopGroup eventLoops;
    private final Timer saveThread = new Timer("Reconnect Saver");
    private final Timer metricsThread = new Timer("Metrics Thread");
    private final Collection<Channel> listeners = new HashSet<>();
    private final Map<String, UserConnection> connections = new CaseInsensitiveMap<>();
    private final Map<UUID, UserConnection> connectionsByOfflineUUID = new HashMap<>();
    private final Map<UUID, UserConnection> connectionsByUUID = new HashMap<>();
    private final ReadWriteLock connectionLock = new ReentrantReadWriteLock();
    private final ReentrantLock shutdownLock = new ReentrantLock();
    public final PluginManager pluginManager;
    private ReconnectHandler reconnectHandler;
    private ConfigurationAdapter configurationAdapter = new YamlConfig();
    private final Collection<String> pluginChannels = new HashSet<>();
    private final BungeeFile pluginsFolder;
    private final BungeeScheduler scheduler = new BungeeScheduler();
    private final ConsoleReader consoleReader;
    private final Logger logger;
    public final Gson gson = (new GsonBuilder()).registerTypeAdapter(BaseComponent.class, new ComponentSerializer()).registerTypeAdapter(TextComponent.class, new TextComponentSerializer()).registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer()).registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer()).registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer()).registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer()).registerTypeAdapter(ServerPing.PlayerInfo.class, new PlayerInfoSerializer()).registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter()).create();
    private ConnectionThrottle connectionThrottle;
    private final ModuleManager moduleManager = new ModuleManager();
    private static Properties properties;

    public static BungeeCord getInstance() {
        return (BungeeCord) ProxyServer.getInstance();
    }

    public static Properties getProperties() {
        return properties;
    }

    public MinestrumBungee(Logger logger, Properties properties) throws IOException {
        setInstance(this);
        MinestrumBungee.properties = properties;

        this.pluginsFolder = new BungeeFile("plugins");
        this.registerChannel("BungeeCord");
        Preconditions.checkState((new BungeeFile(".")).getAbsolutePath().indexOf(33) == -1, "Cannot use BungeeCord in directory with ! in path.");

        try {
            this.baseBundle = ResourceBundle.getBundle("messages");
        } catch (MissingResourceException var2) {
            this.baseBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
        }

        this.reloadMessages();
        System.setProperty("library.jansi.version", "BungeeCord");
        AnsiConsole.systemInstall();
        this.consoleReader = new ConsoleReader();
        this.consoleReader.setExpandEvents(false);
        this.consoleReader.addCompleter(new ConsoleCommandCompleter(this));
        this.logger = logger;
        JDK14LoggerFactory.LOGGER = this.getLogger();
        System.setErr(new PrintStream(new LoggingOutputStream(this.logger, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggingOutputStream(this.logger, Level.INFO), true));
        this.pluginManager = new PluginManager(this);
        this.getPluginManager().registerCommand(null, new CommandReload());
        this.getPluginManager().registerCommand(null, new CommandEnd());
        this.getPluginManager().registerCommand(null, new CommandIP());
        this.getPluginManager().registerCommand(null, new CommandBungee());
        this.getPluginManager().registerCommand(null, new CommandPerms());
        if (!Boolean.getBoolean("net.md_5.bungee.native.disable")) {
            if (EncryptionUtil.nativeFactory.load()) {
                this.logger.info("Using mbed TLS based native cipher.");
            } else {
                this.logger.info("Using standard Java JCE cipher.");
            }

            if (CompressFactory.zlib.load()) {
                this.logger.info("Using zlib based native compressor.");
            } else {
                this.logger.info("Using standard Java compressor.");
            }
        }

    }

    public void start() throws Exception {
        System.setProperty("io.netty.selectorAutoRebuildThreshold", "0");
        if (System.getProperty("io.netty.leakDetectionLevel") == null && System.getProperty("io.netty.leakDetection.level") == null) {
            ResourceLeakDetector.setLevel(io.netty.util.ResourceLeakDetector.Level.DISABLED);
        }

        this.eventLoops = PipelineUtils.newEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty IO Thread #%1$d").build());
        BungeeFile moduleDirectory = new BungeeFile("modules");
        this.moduleManager.load(this, moduleDirectory);
        this.pluginManager.detectPlugins(moduleDirectory);
        this.pluginsFolder.mkdir();
        this.pluginManager.detectPlugins(this.pluginsFolder);
        this.pluginManager.loadPlugins();
        this.config.load();
        if (this.config.isForgeSupport()) {
            this.registerChannel("FML");
            this.registerChannel("FML|HS");
            this.registerChannel("FORGE");
            this.getLogger().warning("MinecraftForge support is currently unmaintained and may have unresolved issues. Please use at your own risk.");
        }

        this.isRunning = true;
        this.pluginManager.enablePlugins();
        if (this.config.getThrottle() > 0) {
            this.connectionThrottle = new ConnectionThrottle(this.config.getThrottle(), this.config.getThrottleLimit());
        }

        this.startListeners();
        this.saveThread.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (MinestrumBungee.this.getReconnectHandler() != null) {
                    MinestrumBungee.this.getReconnectHandler().save();
                }

            }
        }, 0L, TimeUnit.MINUTES.toMillis(5L));
        this.metricsThread.scheduleAtFixedRate(new Metrics(), 0L, TimeUnit.MINUTES.toMillis(10L));
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                MinestrumBungee.this.independentThreadStop(
                        MinestrumBungee.this.getTranslation("restart"), false))
        );
    }

    public void startListeners() {
        for (ListenerInfo info : this.config.getListeners()) {
            if (info.isProxyProtocol()) {
                this.getLogger().log(Level.WARNING, "Using PROXY protocol for listener {0}, please ensure this listener is adequately firewalled.", info.getSocketAddress());
                if (this.connectionThrottle != null) {
                    this.connectionThrottle = null;
                    this.getLogger().log(Level.WARNING, "Since PROXY protocol is in use, internal connection throttle has been disabled.");
                }
            }

            ChannelFutureListener listener = future -> {
                if (future.isSuccess()) {
                    MinestrumBungee.this.listeners.add(future.channel());
                    MinestrumBungee.this.getLogger().log(Level.INFO, "Listening on {0}", info.getSocketAddress());
                } else {
                    MinestrumBungee.this.getLogger().log(Level.WARNING, "Could not bind to host " + info.getSocketAddress(), future.cause());
                }

            };
            ((ServerBootstrap) ((ServerBootstrap) ((ServerBootstrap) (new ServerBootstrap()).channel(PipelineUtils.getServerChannel(info.getSocketAddress()))).option(ChannelOption.SO_REUSEADDR, true)).childAttr(PipelineUtils.LISTENER, info).childHandler(PipelineUtils.SERVER_CHILD).group(this.eventLoops).localAddress(info.getSocketAddress())).bind().addListener(listener);
            if (info.isQueryEnabled()) {
                Preconditions.checkArgument(info.getSocketAddress() instanceof InetSocketAddress, "Can only create query listener on UDP address");
                ChannelFutureListener bindListener = future -> {
                    if (future.isSuccess()) {
                        MinestrumBungee.this.listeners.add(future.channel());
                        MinestrumBungee.this.getLogger().log(Level.INFO, "Started query on {0}", future.channel().localAddress());
                    } else {
                        MinestrumBungee.this.getLogger().log(Level.WARNING, "Could not bind to host " + info.getSocketAddress(), future.cause());
                    }

                };
                (new RemoteQuery(this, info)).start(PipelineUtils.getDatagramChannel(), new InetSocketAddress(info.getHost().getAddress(), info.getQueryPort()), this.eventLoops, bindListener);
            }
        }

    }

    public void stopListeners() {
        for (Channel listener : this.listeners) {
            this.getLogger().log(Level.INFO, "Closing listener {0}", listener);

            try {
                listener.close().syncUninterruptibly();
            } catch (ChannelException var4) {
                this.getLogger().severe("Could not close listen thread");
            }
        }

        this.listeners.clear();
    }

    public void stop() {
        this.stop(this.getTranslation("restart"));
    }

    public void stop(final String reason) {
        (new Thread("Shutdown Thread") {
            public void run() {
                MinestrumBungee.this.independentThreadStop(reason, true);
            }
        }).start();
    }

    private void independentThreadStop(String reason, boolean callSystemExit) {
        this.shutdownLock.lock();
        if (!this.isRunning) {
            this.shutdownLock.unlock();
        } else {
            this.isRunning = false;
            this.stopListeners();
            this.getLogger().info("Closing pending connections");
            this.connectionLock.readLock().lock();

            try {
                this.getLogger().log(Level.INFO, "Disconnecting {0} connections", this.connections.size());

                for (UserConnection user : this.connections.values()) {
                    user.disconnect(reason);
                }

            } finally {
                this.connectionLock.readLock().unlock();
            }

            try {
                Thread.sleep(500L);
            } catch (InterruptedException ignored) {
            }

            if (this.reconnectHandler != null) {
                this.getLogger().info("Saving reconnect locations");
                this.reconnectHandler.save();
                this.reconnectHandler.close();
            }

            this.saveThread.cancel();
            this.metricsThread.cancel();
            this.getLogger().info("Disabling plugins");


            for (Plugin plugin : Lists.reverse(new ArrayList<>(this.pluginManager.getPlugins()))) {
                try {
                    plugin.onDisable();
                    Handler[] var5 = plugin.getLogger().getHandlers();
                    int var6 = var5.length;

                    for (Handler handler : var5) {
                        handler.close();
                    }
                } catch (Throwable var15) {
                    this.getLogger().log(Level.SEVERE, "Exception disabling plugin " + plugin.getDescription().getName(), var15);
                }

                this.getScheduler().cancel(plugin);
                plugin.getExecutorService().shutdownNow();
            }

            this.getLogger().info("Closing IO threads");
            this.eventLoops.shutdownGracefully();

            try {
                this.eventLoops.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException ignored) {
            }

            this.getLogger().info("Thank you and goodbye");
            Handler[] var17 = this.getLogger().getHandlers();
            int var19 = var17.length;

            for (Handler handler : var17) {
                handler.close();
            }

            this.shutdownLock.unlock();
            if (callSystemExit) {
                System.exit(0);
            }

        }
    }

    public void broadcast(DefinedPacket packet) {
        this.connectionLock.readLock().lock();

        try {
            for (UserConnection con : this.connections.values()) {
                con.unsafe().sendPacket(packet);
            }
        } finally {
            this.connectionLock.readLock().unlock();
        }

    }

    public String getName() {
        return "BungeeCord";
    }

    public String getVersion() {
        return MinestrumBungee.class.getPackage().getImplementationVersion() == null ? "unknown" : MinestrumBungee.class.getPackage().getImplementationVersion();
    }

    public void reloadMessages() {
        BungeeFile file = new BungeeFile("messages.properties");
        if (file.isFile()) {
            try {
                FileReader rd = new FileReader(file);

                try {
                    this.customBundle = new PropertyResourceBundle(rd);
                } catch (Throwable var6) {
                    try {
                        rd.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }

                    throw var6;
                }

                rd.close();
            } catch (IOException var7) {
                this.getLogger().log(Level.SEVERE, "Could not load custom messages.properties", var7);
            }
        }

    }

    public String getTranslation(String name, Object... args) {
        String translation = "<translation '" + name + "' missing>";

        try {
            translation = MessageFormat.format(this.customBundle != null && this.customBundle.containsKey(name) ? this.customBundle.getString(name) : this.baseBundle.getString(name), args);
        } catch (MissingResourceException ignored) {
        }

        return translation;
    }

    public Collection<ProxiedPlayer> getPlayers() {
        this.connectionLock.readLock().lock();

        Collection<ProxiedPlayer> players;
        try {
            players = Collections.unmodifiableCollection(new HashSet<>(this.connections.values()));
        } finally {
            this.connectionLock.readLock().unlock();
        }

        return players;
    }

    public int getOnlineCount() {
        return this.connections.size();
    }

    public ProxiedPlayer getPlayer(String name) {
        this.connectionLock.readLock().lock();

        ProxiedPlayer var2;
        try {
            var2 = (ProxiedPlayer)this.connections.get(name);
        } finally {
            this.connectionLock.readLock().unlock();
        }

        return var2;
    }

    public UserConnection getPlayerByOfflineUUID(UUID name) {
        this.connectionLock.readLock().lock();

        UserConnection var2;
        try {
            var2 = (UserConnection)this.connectionsByOfflineUUID.get(name);
        } finally {
            this.connectionLock.readLock().unlock();
        }

        return var2;
    }

    public ProxiedPlayer getPlayer(UUID uuid) {
        this.connectionLock.readLock().lock();

        ProxiedPlayer var2;
        try {
            var2 = (ProxiedPlayer)this.connectionsByUUID.get(uuid);
        } finally {
            this.connectionLock.readLock().unlock();
        }

        return var2;
    }

    public Map<String, ServerInfo> getServers() {
        return this.config.getServers();
    }

    public ServerInfo getServerInfo(String name) {
        return (ServerInfo)this.getServers().get(name);
    }

    public void registerChannel(String channel) {
        synchronized(this.pluginChannels) {
            this.pluginChannels.add(channel);
        }
    }

    public void unregisterChannel(String channel) {
        synchronized(this.pluginChannels) {
            this.pluginChannels.remove(channel);
        }
    }

    public Collection<String> getChannels() {
        synchronized(this.pluginChannels) {
            return Collections.unmodifiableCollection(this.pluginChannels);
        }
    }

    public PluginMessage registerChannels(int protocolVersion) {
        return protocolVersion >= 393 ? new PluginMessage("minecraft:register", Util.format(Iterables.transform(this.pluginChannels, PluginMessage.MODERNISE), "\u0000").getBytes(Charsets.UTF_8), false) : new PluginMessage("REGISTER", Util.format(this.pluginChannels, "\u0000").getBytes(Charsets.UTF_8), false);
    }

    public int getProtocolVersion() {
        return (Integer) ProtocolConstants.SUPPORTED_VERSION_IDS.get(ProtocolConstants.SUPPORTED_VERSION_IDS.size() - 1);
    }

    public String getGameVersion() {
        return (String)ProtocolConstants.SUPPORTED_VERSIONS.get(0) + "-" + (String)ProtocolConstants.SUPPORTED_VERSIONS.get(ProtocolConstants.SUPPORTED_VERSIONS.size() - 1);
    }

    public ServerInfo constructServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
        return this.constructServerInfo(name, (SocketAddress)address, motd, restricted);
    }

    public ServerInfo constructServerInfo(String name, SocketAddress address, String motd, boolean restricted) {
        return new BungeeServerInfo(name, address, motd, restricted);
    }

    public CommandSender getConsole() {
        return ConsoleCommandSender.getInstance();
    }

    public void broadcast(String message) {
        this.broadcast(TextComponent.fromLegacyText(message));
    }

    public void broadcast(BaseComponent... message) {
        this.getConsole().sendMessage(BaseComponent.toLegacyText(message));

        for (ProxiedPlayer player : this.getPlayers()) {
            player.sendMessage(message);
        }

    }

    public void broadcast(BaseComponent message) {
        this.getConsole().sendMessage(message.toLegacyText());

        for (ProxiedPlayer player : this.getPlayers()) {
            player.sendMessage(message);
        }

    }

    public void addConnection(UserConnection con) {
        this.connectionLock.writeLock().lock();

        try {
            this.connections.put(con.getName(), con);
            this.connectionsByUUID.put(con.getUniqueId(), con);
            this.connectionsByOfflineUUID.put(con.getPendingConnection().getOfflineId(), con);
        } finally {
            this.connectionLock.writeLock().unlock();
        }

    }

    public void removeConnection(UserConnection con) {
        this.connectionLock.writeLock().lock();

        try {
            if (this.connections.get(con.getName()) == con) {
                this.connections.remove(con.getName());
                this.connectionsByUUID.remove(con.getUniqueId());
                this.connectionsByOfflineUUID.remove(con.getPendingConnection().getOfflineId());
            }
        } finally {
            this.connectionLock.writeLock().unlock();
        }

    }

    public Collection<String> getDisabledCommands() {
        return this.config.getDisabledCommands();
    }

    public Collection<ProxiedPlayer> matchPlayer(final String partialName) {
        Preconditions.checkNotNull(partialName, "partialName");
        ProxiedPlayer exactMatch = this.getPlayer(partialName);
        return exactMatch != null ? Collections.singleton(exactMatch) : Sets.newHashSet(this.getPlayers().stream().filter(input -> input != null && input.getName().toLowerCase(Locale.ROOT).startsWith(partialName.toLowerCase(Locale.ROOT))).collect(Collectors.toList()));
    }

    public Title createTitle() {
        return new BungeeTitle();
    }

    public Configuration getConfig() {
        return this.config;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public ReconnectHandler getReconnectHandler() {
        return this.reconnectHandler;
    }

    public void setReconnectHandler(ReconnectHandler reconnectHandler) {
        this.reconnectHandler = reconnectHandler;
    }

    public ConfigurationAdapter getConfigurationAdapter() {
        return this.configurationAdapter;
    }

    public void setConfigurationAdapter(ConfigurationAdapter configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    public BungeeFile getPluginsFolder() {
        return this.pluginsFolder;
    }

    public BungeeScheduler getScheduler() {
        return this.scheduler;
    }

    public ConsoleReader getConsoleReader() {
        return this.consoleReader;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public ConnectionThrottle getConnectionThrottle() {
        return this.connectionThrottle;
    }
}
