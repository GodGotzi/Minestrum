package net.gotzi.bungee.connection;

import net.gotzi.bungee.api.AbstractReconnectHandler;
import net.gotzi.bungee.api.Callback;
import net.gotzi.bungee.api.ChatColor;
import net.gotzi.bungee.api.ServerPing;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.bungee.api.connection.PendingConnection;
import net.gotzi.bungee.api.connection.ProxiedPlayer;
import net.gotzi.bungee.api.event.LoginEvent;
import net.gotzi.bungee.api.event.PlayerHandshakeEvent;
import net.gotzi.bungee.api.event.ServerConnectEvent;
import net.gotzi.bungee.http.HttpClient;
import net.gotzi.bungee.netty.ChannelWrapper;
import net.gotzi.bungee.netty.HandlerBoss;
import net.gotzi.bungee.netty.PacketHandler;
import net.gotzi.bungee.netty.PipelineUtils;
import net.gotzi.bungee.netty.cipher.CipherDecoder;
import net.gotzi.bungee.netty.cipher.CipherEncoder;
import net.gotzi.bungee.protocol.Protocol;
import net.gotzi.bungee.protocol.ProtocolConstants;
import net.gotzi.bungee.util.BufUtil;
import net.gotzi.bungee.util.QuietException;
import net.gotzi.minestrum.api.logging.LogLevel;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.BungeeServerInfo;
import net.gotzi.bungee.EncryptionUtil;
import net.gotzi.bungee.UserConnection;
import net.gotzi.bungee.Util;
import net.gotzi.bungee.api.chat.BaseComponent;
import net.gotzi.bungee.api.chat.TextComponent;
import net.gotzi.bungee.api.config.ListenerInfo;
import net.gotzi.bungee.api.event.PostLoginEvent;
import net.gotzi.bungee.api.event.PreLoginEvent;
import net.gotzi.bungee.api.event.ProxyPingEvent;
import net.gotzi.bungee.chat.ComponentSerializer;
import net.gotzi.bungee.jni.cipher.BungeeCipher;
import net.gotzi.bungee.protocol.DefinedPacket;
import net.gotzi.bungee.protocol.PacketWrapper;
import net.gotzi.bungee.protocol.PlayerPublicKey;
import net.gotzi.bungee.protocol.packet.EncryptionRequest;
import net.gotzi.bungee.protocol.packet.EncryptionResponse;
import net.gotzi.bungee.protocol.packet.Handshake;
import net.gotzi.bungee.protocol.packet.Kick;
import net.gotzi.bungee.protocol.packet.LegacyHandshake;
import net.gotzi.bungee.protocol.packet.LegacyPing;
import net.gotzi.bungee.protocol.packet.LoginPayloadResponse;
import net.gotzi.bungee.protocol.packet.LoginRequest;
import net.gotzi.bungee.protocol.packet.LoginSuccess;
import net.gotzi.bungee.protocol.packet.PingPacket;
import net.gotzi.bungee.protocol.packet.PluginMessage;
import net.gotzi.bungee.protocol.packet.StatusRequest;
import net.gotzi.bungee.protocol.packet.StatusResponse;
import net.gotzi.bungee.util.AllowedCharacters;

@RequiredArgsConstructor
public class InitialHandler extends PacketHandler implements PendingConnection
{

    private final Bungee bungee;
    private ChannelWrapper ch;
    @Getter
    private final ListenerInfo listener;
    @Getter
    private Handshake handshake;
    @Getter
    private LoginRequest loginRequest;
    private EncryptionRequest request;
    @Getter
    private PluginMessage brandMessage;
    @Getter
    private final Set<String> registeredChannels = new HashSet<>();
    private State thisState = State.HANDSHAKE;
    private final Unsafe unsafe = new Unsafe()
    {
        @Override
        public void sendPacket(DefinedPacket packet)
        {
            ch.write( packet );
        }
    };

    @Getter
    private boolean onlineMode = Bungee.getInstance().config.isOnlineMode();
    @Getter
    private InetSocketAddress virtualHost;
    private String name;
    @Getter
    private UUID uniqueId;
    @Getter
    private UUID offlineId;
    @Getter
    private LoginResult loginProfile;
    @Getter
    private boolean legacy;
    @Getter
    private String extraDataInHandshake = "";

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception
    {
        return !ch.isClosing();
    }

    private enum State
    {

        HANDSHAKE, STATUS, PING, USERNAME, ENCRYPT, FINISHING;
    }

    private boolean canSendKickMessage()
    {
        return thisState == State.USERNAME || thisState == State.ENCRYPT || thisState == State.FINISHING;
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception
    {
        this.ch = channel;
    }

    @Override
    public void exception(Throwable t) throws Exception
    {
        if ( canSendKickMessage() )
        {
            disconnect( ChatColor.RED + Util.exception( t ) );
        } else
        {
            ch.close();
        }
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception
    {
        if ( packet.packet == null )
        {
            throw new QuietException( "Unexpected packet received during login process! " + BufUtil.dump( packet.buf, 16 ) );
        }
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception
    {
        this.relayMessage( pluginMessage );
    }

    @Override
    public void handle(LegacyHandshake legacyHandshake) throws Exception
    {
        this.legacy = true;
        ch.close( bungee.getTranslation( "outdated_client", bungee.getGameVersion() ) );
    }

    @Override
    public void handle(LegacyPing ping) throws Exception
    {
        this.legacy = true;
        final boolean v1_5 = ping.isV1_5();

        ServerInfo forced = AbstractReconnectHandler.getForcedHost( this );
        final String motd = ( forced != null ) ? forced.getMotd() : listener.getMotd();
        final int protocol = bungee.getProtocolVersion();

        Callback<ServerPing> pingBack = (result, error) -> {
            if ( error != null )
            {
                result = getPingInfo( bungee.getTranslation( "ping_cannot_connect" ), protocol );
                bungee.getLogger().log(LogLevel.WARNING, "Error pinging remote server", error );
            }

            Callback<ProxyPingEvent> callback = (result1, error1) -> {
                if (ch.isClosed()) {
                    return;
                }

                ServerPing legacy = result1.getResponse();
                String kickMessage;

                if (v1_5) {
                    kickMessage = ChatColor.DARK_BLUE
                            + "\00" + 127
                            + '\00' + legacy.getVersion().getName()
                            + '\00' + getFirstLine(legacy.getDescription())
                            + '\00' + ((legacy.getPlayers() != null) ? legacy.getPlayers().getOnline() : "-1")
                            + '\00' + ((legacy.getPlayers() != null) ? legacy.getPlayers().getMax() : "-1");
                } else {
                    // Clients <= 1.3 don't support colored motds because the color char is used as delimiter
                    kickMessage = ChatColor.stripColor(getFirstLine(legacy.getDescription()))
                            + '\u00a7' + ((legacy.getPlayers() != null) ? legacy.getPlayers().getOnline() : "-1")
                            + '\u00a7' + ((legacy.getPlayers() != null) ? legacy.getPlayers().getMax() : "-1");
                }

                ch.close(kickMessage);
            };

            bungee.getPluginManager().callEvent( new ProxyPingEvent( InitialHandler.this, result, callback ) );
        };

        if ( forced != null && listener.isPingPassthrough() )
        {
            ( (BungeeServerInfo) forced ).ping( pingBack, bungee.getProtocolVersion() );
        } else
        {
            pingBack.done( getPingInfo( motd, protocol ), null );
        }
    }

    private static String getFirstLine(String str)
    {
        int pos = str.indexOf( '\n' );
        return pos == -1 ? str : str.substring( 0, pos );
    }

    private ServerPing getPingInfo(String motd, int protocol)
    {
        return new ServerPing(
                new ServerPing.Protocol( bungee.getName() + " " + bungee.getGameVersion(), protocol ),
                new ServerPing.Players( listener.getMaxPlayers(), bungee.getOnlineCount(), null ),
                motd, Bungee.getInstance().config.getFaviconObject()
        );
    }

    @Override
    public void handle(StatusRequest statusRequest) throws Exception
    {
        Preconditions.checkState( thisState == State.STATUS, "Not expecting STATUS" );

        ServerInfo forced = AbstractReconnectHandler.getForcedHost( this );
        final String motd = ( forced != null ) ? forced.getMotd() : listener.getMotd();
        final int protocol = ( ProtocolConstants.SUPPORTED_VERSION_IDS.contains( handshake.getProtocolVersion() ) ) ? handshake.getProtocolVersion() : bungee.getProtocolVersion();

        Callback<ServerPing> pingBack = new Callback<ServerPing>()
        {
            @Override
            public void done(ServerPing result, Throwable error)
            {
                if ( error != null )
                {
                    result = getPingInfo( bungee.getTranslation( "ping_cannot_connect" ), protocol );
                    bungee.getLogger().log( LogLevel.WARNING, "Error pinging remote server", error );
                }

                Callback<ProxyPingEvent> callback = new Callback<ProxyPingEvent>()
                {
                    @Override
                    public void done(ProxyPingEvent pingResult, Throwable error)
                    {
                        Gson gson = Bungee.getInstance().gson;
                        unsafe.sendPacket( new StatusResponse( gson.toJson( pingResult.getResponse() ) ) );
                        if ( bungee.getConnectionThrottle() != null )
                        {
                            bungee.getConnectionThrottle().unthrottle( getSocketAddress() );
                        }
                    }
                };

                bungee.getPluginManager().callEvent( new ProxyPingEvent( InitialHandler.this, result, callback ) );
            }
        };

        if ( forced != null && listener.isPingPassthrough() )
        {
            ( (BungeeServerInfo) forced ).ping( pingBack, handshake.getProtocolVersion() );
        } else
        {
            pingBack.done( getPingInfo( motd, protocol ), null );
        }

        thisState = State.PING;
    }

    @Override
    public void handle(PingPacket ping) throws Exception
    {
        Preconditions.checkState( thisState == State.PING, "Not expecting PING" );
        unsafe.sendPacket( ping );
        disconnect( "" );
    }

    @Override
    public void handle(Handshake handshake) throws Exception
    {
        Preconditions.checkState( thisState == State.HANDSHAKE, "Not expecting HANDSHAKE" );
        this.handshake = handshake;
        ch.setVersion( handshake.getProtocolVersion() );

        // Starting with FML 1.8, a "\0FML\0" token is appended to the handshake. This interferes
        // with Bungee's IP forwarding, so we detect it, and remove it from the host string, for now.
        // We know FML appends \00FML\00. However, we need to also consider that other systems might
        // add their own data to the end of the string. So, we just take everything from the \0 character
        // and save it for later.
        if ( handshake.getHost().contains( "\0" ) )
        {
            String[] split = handshake.getHost().split( "\0", 2 );
            handshake.setHost( split[0] );
            extraDataInHandshake = "\0" + split[1];
        }

        // SRV records can end with a . depending on DNS / client.
        if ( handshake.getHost().endsWith( "." ) )
        {
            handshake.setHost( handshake.getHost().substring( 0, handshake.getHost().length() - 1 ) );
        }

        this.virtualHost = InetSocketAddress.createUnresolved( handshake.getHost(), handshake.getPort() );

        bungee.getPluginManager().callEvent( new PlayerHandshakeEvent( InitialHandler.this, handshake ) );

        switch ( handshake.getRequestedProtocol() )
        {
            case 1:
                // Ping
                if ( bungee.getConfig().isLogPings() )
                {
                    bungee.getLogger().log( LogLevel.INFO, "{0} has pinged", this );
                }
                thisState = State.STATUS;
                ch.setProtocol( Protocol.STATUS );
                break;
            case 2:
                // Login
                bungee.getLogger().log( LogLevel.INFO, "{0} has connected", this );
                thisState = State.USERNAME;
                ch.setProtocol( Protocol.LOGIN );

                if ( !ProtocolConstants.SUPPORTED_VERSION_IDS.contains( handshake.getProtocolVersion() ) )
                {
                    if ( handshake.getProtocolVersion() > bungee.getProtocolVersion() )
                    {
                        disconnect( bungee.getTranslation( "outdated_server", bungee.getGameVersion() ) );
                    } else
                    {
                        disconnect( bungee.getTranslation( "outdated_client", bungee.getGameVersion() ) );
                    }
                    return;
                }
                break;
            default:
                throw new QuietException( "Cannot request protocol " + handshake.getRequestedProtocol() );
        }
    }

    @Override
    public void handle(LoginRequest loginRequest) throws Exception {
        Preconditions.checkState( thisState == State.USERNAME, "Not expecting USERNAME" );

        if ( !AllowedCharacters.isValidName( loginRequest.getData(), onlineMode ) )
        {
            disconnect( bungee.getTranslation( "name_invalid" ) );
            return;
        }

        if ( Bungee.getInstance().config.isEnforceSecureProfile() )
        {
            PlayerPublicKey publicKey = loginRequest.getPublicKey();
            if ( publicKey == null )
            {
                disconnect( bungee.getTranslation( "secure_profile_required" ) );
                return;
            }

            if ( Instant.ofEpochMilli( publicKey.getExpiry() ).isBefore( Instant.now() ) )
            {
                disconnect( bungee.getTranslation( "secure_profile_expired" ) );
                return;
            }

            if ( !EncryptionUtil.check( publicKey ) )
            {
                disconnect( bungee.getTranslation( "secure_profile_invalid" ) );
                return;
            }
        }

        this.loginRequest = loginRequest;

        int limit = Bungee.getInstance().config.getPlayerLimit();
        if ( limit > 0 && bungee.getOnlineCount() >= limit )
        {
            disconnect( bungee.getTranslation( "proxy_full" ) );
            return;
        }

        // If offline mode and they are already on, don't allow connect
        // We can just check by UUID here as names are based on UUID
        if ( !isOnlineMode() && bungee.getPlayer( getUniqueId() ) != null )
        {
            disconnect( bungee.getTranslation( "already_connected_proxy" ) );
            return;
        }
        Callback<PreLoginEvent> callback = (result, error) -> {
            if ( result.isCancelled() )
            {
                BaseComponent[] reason = result.getCancelReasonComponents();
                disconnect( ( reason != null ) ? reason : TextComponent.fromLegacyText( bungee.getTranslation( "kick_message" ) ) );
                return;
            }
            if ( ch.isClosed() )
            {
                return;
            }
            if (onlineMode) {
                thisState = State.ENCRYPT;
                unsafe().sendPacket( request = EncryptionUtil.encryptRequest() );
            } else {
                thisState = State.FINISHING;
                finish();
            }
        };

        // fire pre login event
        bungee.getPluginManager().callEvent( new PreLoginEvent( InitialHandler.this, callback ) );
    }

    @Override
    public void handle(final EncryptionResponse encryptResponse) throws Exception {

        Preconditions.checkState( thisState == State.ENCRYPT, "Not expecting ENCRYPT" );
        Preconditions.checkState( EncryptionUtil.check( loginRequest.getPublicKey(), encryptResponse, request ), "Invalid verification" );

        SecretKey sharedKey = EncryptionUtil.getSecret( encryptResponse, request );
        BungeeCipher decrypt = EncryptionUtil.getCipher( false, sharedKey );
        ch.addBefore( PipelineUtils.FRAME_DECODER, PipelineUtils.DECRYPT_HANDLER, new CipherDecoder( decrypt ) );
        BungeeCipher encrypt = EncryptionUtil.getCipher( true, sharedKey );
        ch.addBefore( PipelineUtils.FRAME_PREPENDER, PipelineUtils.ENCRYPT_HANDLER, new CipherEncoder( encrypt ) );

        String encName = URLEncoder.encode( InitialHandler.this.getName(), "UTF-8" );

        MessageDigest sha = MessageDigest.getInstance( "SHA-1" );
        for ( byte[] bit : new byte[][]
        {
            request.getServerId().getBytes( "ISO_8859_1" ), sharedKey.getEncoded(), EncryptionUtil.keys.getPublic().getEncoded()
        } )
        {
            sha.update( bit );
        }
        String encodedHash = URLEncoder.encode( new BigInteger( sha.digest() ).toString( 16 ), "UTF-8" );

        String preventProxy = ( Bungee.getInstance().config.isPreventProxyConnections() && getSocketAddress() instanceof InetSocketAddress ) ? "&ip=" + URLEncoder.encode( getAddress().getAddress().getHostAddress(), "UTF-8" ) : "";
        String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName + "&serverId=" + encodedHash + preventProxy;

        Callback<String> handler = new Callback<String>()
        {
            @Override
            public void done(String result, Throwable error)
            {
                if ( error == null )
                {
                    LoginResult obj = Bungee.getInstance().gson.fromJson( result, LoginResult.class );
                    if ( obj != null && obj.getId() != null )
                    {
                        loginProfile = obj;
                        name = obj.getName();
                        uniqueId = Util.getUUID( obj.getId() );
                        finish();
                        return;
                    }
                    disconnect( bungee.getTranslation( "offline_mode_player" ) );
                } else
                {
                    disconnect( bungee.getTranslation( "mojang_fail" ) );
                    bungee.getLogger().log( Level.SEVERE, "Error authenticating " + getName() + " with minecraft.net", error );
                }
            }
        };
        thisState = State.FINISHING;
        HttpClient.get( authURL, ch.getHandle().eventLoop(), handler );
    }

    private void finish()
    {
        if ( isOnlineMode() )
        {
            // Check for multiple connections
            // We have to check for the old name first
            ProxiedPlayer oldName = bungee.getPlayer( getName() );
            if ( oldName != null )
            {
                // TODO See #1218
                oldName.disconnect( bungee.getTranslation( "already_connected_proxy" ) );
            }
            // And then also for their old UUID
            ProxiedPlayer oldID = bungee.getPlayer( getUniqueId() );
            if ( oldID != null )
            {
                // TODO See #1218
                oldID.disconnect( bungee.getTranslation( "already_connected_proxy" ) );
            }
        } else
        {
            // In offline mode the existing user stays and we kick the new one
            ProxiedPlayer oldName = bungee.getPlayer( getName() );
            if ( oldName != null )
            {
                // TODO See #1218
                disconnect( bungee.getTranslation( "already_connected_proxy" ) );
                return;
            }

        }

        offlineId = UUID.nameUUIDFromBytes( ( "OfflinePlayer:" + getName() ).getBytes( Charsets.UTF_8 ) );
        if ( uniqueId == null )
        {
            uniqueId = offlineId;
        }

        Callback<LoginEvent> complete = new Callback<LoginEvent>()
        {
            @Override
            public void done(LoginEvent result, Throwable error)
            {
                if ( result.isCancelled() )
                {
                    BaseComponent[] reason = result.getCancelReasonComponents();
                    disconnect( ( reason != null ) ? reason : TextComponent.fromLegacyText( bungee.getTranslation( "kick_message" ) ) );
                    return;
                }
                if ( ch.isClosed() )
                {
                    return;
                }

                ch.getHandle().eventLoop().execute( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if ( !ch.isClosing() )
                        {
                            UserConnection userCon = new UserConnection( bungee, ch, getName(), InitialHandler.this );
                            userCon.setCompressionThreshold( Bungee.getInstance().config.getCompressionThreshold() );
                            userCon.init();

                            unsafe.sendPacket( new LoginSuccess( getUniqueId(), getName(), ( loginProfile == null ) ? null : loginProfile.getProperties() ) );
                            ch.setProtocol( Protocol.GAME );

                            ch.getHandle().pipeline().get( HandlerBoss.class ).setHandler( new UpstreamBridge( bungee, userCon ) );
                            bungee.getPluginManager().callEvent( new PostLoginEvent( userCon ) );
                            ServerInfo server;
                            if ( bungee.getReconnectHandler() != null )
                            {
                                server = bungee.getReconnectHandler().getServer( userCon );
                            } else
                            {
                                server = AbstractReconnectHandler.getForcedHost( InitialHandler.this );
                            }
                            if ( server == null )
                            {
                                server = bungee.getServerInfo( listener.getDefaultServer() );
                            }

                            userCon.connect( server, null, true, ServerConnectEvent.Reason.JOIN_PROXY );
                        }
                    }
                } );
            }
        };

        // fire login event
        bungee.getPluginManager().callEvent( new LoginEvent( InitialHandler.this, complete ) );
    }

    @Override
    public void handle(LoginPayloadResponse response) throws Exception
    {
        disconnect( "Unexpected custom LoginPayloadResponse" );
    }

    @Override
    public void disconnect(String reason)
    {
        if ( canSendKickMessage() )
        {
            disconnect( TextComponent.fromLegacyText( reason ) );
        } else
        {
            ch.close();
        }
    }

    @Override
    public void disconnect(final BaseComponent... reason)
    {
        if ( canSendKickMessage() )
        {
            ch.delayedClose( new Kick( ComponentSerializer.toString( reason ) ) );
        } else
        {
            ch.close();
        }
    }

    @Override
    public void disconnect(BaseComponent reason)
    {
        disconnect( new BaseComponent[]
        {
            reason
        } );
    }

    @Override
    public String getName()
    {
        return ( name != null ) ? name : ( loginRequest == null ) ? null : loginRequest.getData();
    }

    @Override
    public int getVersion()
    {
        return ( handshake == null ) ? -1 : handshake.getProtocolVersion();
    }

    @Override
    public InetSocketAddress getAddress()
    {
        return (InetSocketAddress) getSocketAddress();
    }

    @Override
    public SocketAddress getSocketAddress()
    {
        return ch.getRemoteAddress();
    }

    @Override
    public Unsafe unsafe()
    {
        return unsafe;
    }

    @Override
    public void setOnlineMode(boolean onlineMode)
    {
        Preconditions.checkState( thisState == State.USERNAME, "Can only set online mode status whilst state is username" );
        this.onlineMode = onlineMode;
    }

    @Override
    public void setUniqueId(UUID uuid)
    {
        Preconditions.checkState( thisState == State.USERNAME, "Can only set uuid while state is username" );
        Preconditions.checkState( !onlineMode, "Can only set uuid when online mode is false" );
        this.uniqueId = uuid;
    }

    @Override
    public String getUUID()
    {
        return uniqueId.toString().replace( "-", "" );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );

        String currentName = getName();
        if ( currentName != null )
        {
            sb.append( currentName );
            sb.append( ',' );
        }

        sb.append( getSocketAddress() );
        sb.append( "] <-> InitialHandler" );

        return sb.toString();
    }

    @Override
    public boolean isConnected()
    {
        return !ch.isClosed();
    }

    public void relayMessage(PluginMessage input) throws Exception
    {
        if ( input.getTag().equals( "REGISTER" ) || input.getTag().equals( "minecraft:register" ) )
        {
            String content = new String( input.getData(), StandardCharsets.UTF_8 );

            for ( String id : content.split( "\0" ) )
            {
                Preconditions.checkState( registeredChannels.size() < 128, "Too many registered channels" );
                Preconditions.checkArgument( id.length() < 128, "Channel name too long" );

                registeredChannels.add( id );
            }
        } else if ( input.getTag().equals( "UNREGISTER" ) || input.getTag().equals( "minecraft:unregister" ) )
        {
            String content = new String( input.getData(), StandardCharsets.UTF_8 );

            for ( String id : content.split( "\0" ) )
            {
                registeredChannels.remove( id );
            }
        } else if ( input.getTag().equals( "MC|Brand" ) || input.getTag().equals( "minecraft:brand" ) )
        {
            brandMessage = input;
        }
    }
}
