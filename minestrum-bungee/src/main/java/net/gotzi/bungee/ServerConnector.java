package net.gotzi.bungee;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.bungee.api.score.Objective;
import net.gotzi.bungee.api.score.Scoreboard;
import net.gotzi.bungee.netty.ChannelWrapper;
import net.gotzi.bungee.netty.HandlerBoss;
import net.gotzi.bungee.netty.PacketHandler;
import net.gotzi.bungee.protocol.Protocol;
import net.gotzi.bungee.protocol.packet.*;
import net.gotzi.bungee.protocol.packet.Team;
import net.gotzi.bungee.util.AddressUtil;
import net.gotzi.bungee.util.QuietException;
import net.gotzi.bungee.api.ChatColor;
import net.gotzi.bungee.api.ProxyServer;
import net.gotzi.bungee.api.event.ServerConnectEvent;
import net.gotzi.bungee.api.event.ServerConnectedEvent;
import net.gotzi.bungee.api.event.ServerKickEvent;
import net.gotzi.bungee.api.event.ServerSwitchEvent;
import net.gotzi.bungee.api.score.Score;
import net.gotzi.bungee.chat.ComponentSerializer;
import net.gotzi.bungee.connection.CancelSendSignal;
import net.gotzi.bungee.connection.DownstreamBridge;
import net.gotzi.bungee.connection.LoginResult;
import net.gotzi.bungee.protocol.DefinedPacket;
import net.gotzi.bungee.protocol.PacketWrapper;
import net.gotzi.bungee.protocol.ProtocolConstants;
import net.gotzi.bungee.util.BufUtil;
import net.gotzi.bungee.protocol.packet.Kick;

@RequiredArgsConstructor
public class ServerConnector extends PacketHandler
{

    private final ProxyServer bungee;
    private ChannelWrapper ch;
    private final UserConnection user;
    private final BungeeServerInfo target;
    private State thisState = State.LOGIN_SUCCESS;
    private boolean obsolete;

    private enum State
    {

        LOGIN_SUCCESS, ENCRYPT_RESPONSE, LOGIN, FINISHED;
    }

    @Override
    public void exception(Throwable t) throws Exception
    {
        if ( obsolete )
        {
            return;
        }

        String message = "Exception Connecting:" + Util.exception( t );
        if ( user.getServer() == null )
        {
            user.disconnect( message );
        } else
        {
            user.sendMessage( ChatColor.RED + message );
        }
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception
    {
        this.ch = channel;

        Handshake originalHandshake = user.getPendingConnection().getHandshake();
        Handshake copiedHandshake = new Handshake( originalHandshake.getProtocolVersion(), originalHandshake.getHost(), originalHandshake.getPort(), 2 );

        if ( Bungee.getInstance().config.isIpForward() && user.getSocketAddress() instanceof InetSocketAddress )
        {
            String newHost = copiedHandshake.getHost() + "\00" + AddressUtil.sanitizeAddress( user.getAddress() ) + "\00" + user.getUUID();

            LoginResult profile = user.getPendingConnection().getLoginProfile();
            if ( profile != null && profile.getProperties() != null && profile.getProperties().length > 0 )
            {
                newHost += "\00" + Bungee.getInstance().gson.toJson( profile.getProperties() );
            }
            copiedHandshake.setHost( newHost );
        } else if ( !user.getExtraDataInHandshake().isEmpty() )
        {
            // Only restore the extra data if IP forwarding is off.
            // TODO: Add support for this data with IP forwarding.
            copiedHandshake.setHost( copiedHandshake.getHost() + user.getExtraDataInHandshake() );
        }

        channel.write( copiedHandshake );

        channel.setProtocol( Protocol.LOGIN );
        channel.write( new LoginRequest( user.getName(), null ) );
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception
    {
        user.getPendingConnects().remove( target );
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception
    {
        if ( packet.packet == null )
        {
            throw new QuietException( "Unexpected packet received during server login process!\n" + BufUtil.dump( packet.buf, 16 ) );
        }
    }

    @Override
    public void handle(LoginSuccess loginSuccess) throws Exception
    {
        Preconditions.checkState( thisState == State.LOGIN_SUCCESS, "Not expecting LOGIN_SUCCESS" );
        ch.setProtocol( Protocol.GAME );
        thisState = State.LOGIN;

        // Only reset the Forge client when:
        // 1) The user is switching servers (so has a current server)
        // 2) The handshake is complete
        // 3) The user is currently on a modded server (if we are on a vanilla server,
        //    we may be heading for another vanilla server, so we don't need to reset.)
        //
        // user.getServer() gets the user's CURRENT server, not the one we are trying
        // to connect to.
        //
        // We will reset the connection later if the current server is vanilla, and
        // we need to switch to a modded connection. However, we always need to reset the
        // connection when we have a modded server regardless of where we go - doing it
        // here makes sense.

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(SetCompression setCompression) throws Exception
    {
        ch.setCompressionThreshold( setCompression.getThreshold() );
    }

    @Override
    public void handle(Login login) throws Exception
    {
        Preconditions.checkState( thisState == State.LOGIN, "Not expecting LOGIN" );

        ServerConnection server = new ServerConnection( ch, target );
        ServerConnectedEvent event = new ServerConnectedEvent( user, server );
        bungee.getPluginManager().callEvent( event );

        ch.write( Bungee.getInstance().registerChannels( user.getPendingConnection().getVersion() ) );
        Queue<DefinedPacket> packetQueue = target.getPacketQueue();
        synchronized ( packetQueue )
        {
            while ( !packetQueue.isEmpty() )
            {
                ch.write( packetQueue.poll() );
            }
        }

        PluginMessage brandMessage = user.getPendingConnection().getBrandMessage();
        if ( brandMessage != null )
        {
            ch.write( brandMessage );
        }

        Set<String> registeredChannels = user.getPendingConnection().getRegisteredChannels();
        if ( !registeredChannels.isEmpty() )
        {
            ch.write( new PluginMessage( user.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_13 ? "minecraft:register" : "REGISTER", Joiner.on( "\0" ).join( registeredChannels ).getBytes( StandardCharsets.UTF_8 ), false ) );
        }

        if ( user.getSettings() != null )
        {
            ch.write( user.getSettings() );
        }

        if ( user.getServer() == null || !( login.getDimension() instanceof Integer ) )
        {
            // Once again, first connection
            user.setClientEntityId( login.getEntityId() );
            user.setServerEntityId( login.getEntityId() );

            // Set tab list size, TODO: what shall we do about packet mutability
            Login modLogin = new Login( login.getEntityId(), login.isHardcore(), login.getGameMode(), login.getPreviousGameMode(), login.getWorldNames(), login.getDimensions(), login.getDimension(), login.getWorldName(), login.getSeed(), login.getDifficulty(),
                    (byte) user.getPendingConnection().getListener().getTabListSize(), login.getLevelType(), login.getViewDistance(), login.getSimulationDistance(), login.isReducedDebugInfo(), login.isNormalRespawn(), login.isDebug(), login.isFlat(), login.getDeathLocation() );

            user.unsafe().sendPacket( modLogin );

            if ( user.getServer() != null )
            {
                user.getServer().setObsolete( true );
                user.getTabListHandler().onServerChange();

                user.getServerSentScoreboard().clear();

                for ( UUID bossbar : user.getSentBossBars() )
                {
                    // Send remove bossbar packet
                    user.unsafe().sendPacket( new BossBar( bossbar, 1 ) );
                }
                user.getSentBossBars().clear();

                user.unsafe().sendPacket( new Respawn( login.getDimension(), login.getWorldName(), login.getSeed(), login.getDifficulty(), login.getGameMode(), login.getPreviousGameMode(), login.getLevelType(), login.isDebug(), login.isFlat(), false, login.getDeathLocation() ) );
                user.getServer().disconnect( "Quitting" );
            } else
            {
                ByteBuf brand = ByteBufAllocator.DEFAULT.heapBuffer();
                DefinedPacket.writeString( bungee.getName() + " (" + bungee.getVersion() + ")", brand );
                user.unsafe().sendPacket( new PluginMessage( user.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_13 ? "minecraft:brand" : "MC|Brand", DefinedPacket.toArray( brand ), false) );
                brand.release();
            }

            user.setDimension( login.getDimension() );
        } else
        {
            user.getServer().setObsolete( true );
            user.getTabListHandler().onServerChange();

            Scoreboard serverScoreboard = user.getServerSentScoreboard();
            for ( Objective objective : serverScoreboard.getObjectives() )
            {
                user.unsafe().sendPacket( new ScoreboardObjective( objective.getName(), objective.getValue(), ScoreboardObjective.HealthDisplay.fromString( objective.getType() ), (byte) 1 ) );
            }
            for ( Score score : serverScoreboard.getScores() )
            {
                user.unsafe().sendPacket( new ScoreboardScore( score.getItemName(), (byte) 1, score.getScoreName(), score.getValue() ) );
            }
            for ( net.gotzi.bungee.api.score.Team team : serverScoreboard.getTeams() )
            {
                user.unsafe().sendPacket( new Team( team.getName() ) );
            }
            serverScoreboard.clear();

            for ( UUID bossbar : user.getSentBossBars() )
            {
                // Send remove bossbar packet
                user.unsafe().sendPacket( new BossBar( bossbar, 1 ) );
            }
            user.getSentBossBars().clear();

            // Update debug info from login packet
            user.unsafe().sendPacket( new EntityStatus( user.getClientEntityId(), login.isReducedDebugInfo() ? EntityStatus.DEBUG_INFO_REDUCED : EntityStatus.DEBUG_INFO_NORMAL ) );
            // And immediate respawn
            if ( user.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_15 )
            {
                user.unsafe().sendPacket( new GameState( GameState.IMMEDIATE_RESPAWN, login.isNormalRespawn() ? 0 : 1 ) );
            }

            user.setDimensionChange( true );
            if ( login.getDimension() == user.getDimension() )
            {
                user.unsafe().sendPacket( new Respawn( (Integer) login.getDimension() >= 0 ? -1 : 0, login.getWorldName(), login.getSeed(), login.getDifficulty(), login.getGameMode(), login.getPreviousGameMode(), login.getLevelType(), login.isDebug(), login.isFlat(), false, login.getDeathLocation() ) );
            }

            user.setServerEntityId( login.getEntityId() );
            user.unsafe().sendPacket( new Respawn( login.getDimension(), login.getWorldName(), login.getSeed(), login.getDifficulty(), login.getGameMode(), login.getPreviousGameMode(), login.getLevelType(), login.isDebug(), login.isFlat(), false, login.getDeathLocation() ) );
            if ( user.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_14 )
            {
                user.unsafe().sendPacket( new ViewDistance( login.getViewDistance() ) );
            }
            user.setDimension( login.getDimension() );

            // Remove from old servers
            user.getServer().disconnect( "Quitting" );
        }

        // TODO: Fix this?
        if ( !user.isActive() )
        {
            server.disconnect( "Quitting" );
            // Silly server admins see stack trace and die
            bungee.getLogger().warning( "No client connected for pending server!" );
            return;
        }

        // Add to new server
        // TODO: Move this to the connected() method of DownstreamBridge
        target.addPlayer( user );
        user.getPendingConnects().remove( target );
        user.setServerJoinQueue( null );
        user.setDimensionChange( false );

        ServerInfo from = ( user.getServer() == null ) ? null : user.getServer().getInfo();
        user.setServer( server );
        ch.getHandle().pipeline().get( HandlerBoss.class ).setHandler( new DownstreamBridge( bungee, user, server ) );

        bungee.getPluginManager().callEvent( new ServerSwitchEvent( user, from ) );

        thisState = State.FINISHED;

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(EncryptionRequest encryptionRequest) throws Exception
    {
        throw new QuietException( "Server is online mode!" );
    }

    @Override
    public void handle(Kick kick) throws Exception
    {
        ServerInfo def = user.updateAndGetNextServer( target );
        ServerKickEvent event = new ServerKickEvent( user, target, ComponentSerializer.parse( kick.getMessage() ), def, ServerKickEvent.State.CONNECTING );
        if ( event.getKickReason().toLowerCase( Locale.ROOT ).contains( "outdated" ) && def != null )
        {
            // Pre cancel the event if we are going to try another server
            event.setCancelled( true );
        }
        bungee.getPluginManager().callEvent( event );
        if ( event.isCancelled() && event.getCancelServer() != null )
        {
            obsolete = true;
            user.connect( event.getCancelServer(), ServerConnectEvent.Reason.KICK_REDIRECT );
            throw CancelSendSignal.INSTANCE;
        }

        String message = bungee.getTranslation( "connect_kick", target.getName(), event.getKickReason() );
        if ( user.isDimensionChange() )
        {
            user.disconnect( message );
        } else
        {
            user.sendMessage( message );
        }

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        // We have to forward these to the user, especially with Forge as stuff might break
        // This includes any REGISTER messages we intercepted earlier.
        user.unsafe().sendPacket( pluginMessage );
    }

    @Override
    public void handle(LoginPayloadRequest loginPayloadRequest)
    {
        ch.write( new LoginPayloadResponse( loginPayloadRequest.getId(), null ) );
    }

    @Override
    public String toString()
    {
        return "[" + user.getName() + "] <-> ServerConnector [" + target.getName() + "]";
    }
}
