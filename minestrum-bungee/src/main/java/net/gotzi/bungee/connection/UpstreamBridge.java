package net.gotzi.bungee.connection;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.ServerConnection;
import net.gotzi.bungee.UserConnection;
import net.gotzi.bungee.Util;
import net.gotzi.bungee.api.ProxyServer;
import net.gotzi.bungee.api.connection.ProxiedPlayer;
import net.gotzi.bungee.entitymap.EntityMap;
import net.gotzi.bungee.netty.ChannelWrapper;
import net.gotzi.bungee.netty.PacketHandler;
import net.gotzi.bungee.protocol.ProtocolConstants;
import net.gotzi.bungee.api.event.ChatEvent;
import net.gotzi.bungee.api.event.PlayerDisconnectEvent;
import net.gotzi.bungee.api.event.PluginMessageEvent;
import net.gotzi.bungee.api.event.SettingsChangedEvent;
import net.gotzi.bungee.api.event.TabCompleteEvent;
import net.gotzi.bungee.protocol.PacketWrapper;
import net.gotzi.bungee.protocol.packet.Chat;
import net.gotzi.bungee.protocol.packet.ClientChat;
import net.gotzi.bungee.protocol.packet.ClientCommand;
import net.gotzi.bungee.protocol.packet.ClientSettings;
import net.gotzi.bungee.protocol.packet.KeepAlive;
import net.gotzi.bungee.protocol.packet.PlayerListItem;
import net.gotzi.bungee.protocol.packet.PluginMessage;
import net.gotzi.bungee.protocol.packet.TabCompleteRequest;
import net.gotzi.bungee.protocol.packet.TabCompleteResponse;
import net.gotzi.bungee.util.AllowedCharacters;

public class UpstreamBridge extends PacketHandler
{

    private final ProxyServer bungee;
    private final UserConnection con;

    public UpstreamBridge(ProxyServer bungee, UserConnection con)
    {
        this.bungee = bungee;
        this.con = con;

        Bungee.getInstance().addConnection( con );
        con.getTabListHandler().onConnect();
        con.unsafe().sendPacket( Bungee.getInstance().registerChannels( con.getPendingConnection().getVersion() ) );
    }

    @Override
    public void exception(Throwable t) throws Exception
    {
        con.disconnect( Util.exception( t ) );
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception
    {
        // We lost connection to the client
        PlayerDisconnectEvent event = new PlayerDisconnectEvent( con );
        bungee.getPluginManager().callEvent( event );
        con.getTabListHandler().onDisconnect();
        Bungee.getInstance().removeConnection( con );

        if ( con.getServer() != null )
        {
            // Manually remove from everyone's tab list
            // since the packet from the server arrives
            // too late
            // TODO: This should only done with server_unique
            //       tab list (which is the only one supported
            //       currently)
            PlayerListItem packet = new PlayerListItem();
            packet.setAction( PlayerListItem.Action.REMOVE_PLAYER );
            PlayerListItem.Item item = new PlayerListItem.Item();
            item.setUuid( con.getUniqueId() );
            packet.setItems( new PlayerListItem.Item[]
            {
                item
            } );
            for ( ProxiedPlayer player : con.getServer().getInfo().getPlayers() )
            {
                player.unsafe().sendPacket( packet );
            }
            con.getServer().disconnect( "Quitting" );
        }
    }

    @Override
    public void writabilityChanged(ChannelWrapper channel) throws Exception
    {
        if ( con.getServer() != null )
        {
            Channel server = con.getServer().getCh().getHandle();
            if ( channel.getHandle().isWritable() )
            {
                server.config().setAutoRead( true );
            } else
            {
                server.config().setAutoRead( false );
            }
        }
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception
    {
        return con.getServer() != null || packet.packet instanceof PluginMessage;
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception
    {
        if ( con.getServer() != null )
        {
            EntityMap rewrite = con.getEntityRewrite();
            if ( rewrite != null )
            {
                rewrite.rewriteServerbound( packet.buf, con.getClientEntityId(), con.getServerEntityId(), con.getPendingConnection().getVersion() );
            }
            con.getServer().getCh().write( packet );
        }
    }

    @Override
    public void handle(KeepAlive alive) throws Exception
    {
        ServerConnection.KeepAliveData keepAliveData = con.getServer().getKeepAlives().peek();

        if ( keepAliveData != null && alive.getRandomId() == keepAliveData.getId() )
        {
            Preconditions.checkState( keepAliveData == con.getServer().getKeepAlives().poll(), "keepalive queue mismatch" );
            int newPing = (int) ( System.currentTimeMillis() - keepAliveData.getTime() );
            con.getTabListHandler().onPingChange( newPing );
            con.setPing( newPing );
        } else
        {
            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(Chat chat) throws Exception
    {
        String message = handleChat( chat.getMessage() );
        if ( message != null )
        {
            chat.setMessage( message );
            con.getServer().unsafe().sendPacket( chat );
        }

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(ClientChat chat) throws Exception
    {
        handleChat( chat.getMessage() );
    }

    @Override
    public void handle(ClientCommand command) throws Exception
    {
        handleChat( "/" + command.getCommand() );
    }

    private String handleChat(String message)
    {
        for ( int index = 0, length = message.length(); index < length; index++ )
        {
            char c = message.charAt( index );
            if ( !AllowedCharacters.isChatAllowedCharacter( c ) )
            {
                con.disconnect( bungee.getTranslation( "illegal_chat_characters", Util.unicode( c ) ) );
                throw CancelSendSignal.INSTANCE;
            }
        }

        ChatEvent chatEvent = new ChatEvent( con, con.getServer(), message );
        if ( !bungee.getPluginManager().callEvent( chatEvent ).isCancelled() )
        {
            message = chatEvent.getMessage();
            if ( !chatEvent.isCommand() || !bungee.getPluginManager().dispatchCommand( con, message.substring( 1 ) ) )
            {
                return message;
            }
        }
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(TabCompleteRequest tabComplete) throws Exception
    {
        List<String> suggestions = new ArrayList<>();
        boolean isRegisteredCommand = false;

        if ( tabComplete.getCursor().startsWith( "/" ) )
        {
            isRegisteredCommand = bungee.getPluginManager().dispatchCommand( con, tabComplete.getCursor().substring( 1 ), suggestions );
        }

        TabCompleteEvent tabCompleteEvent = new TabCompleteEvent( con, con.getServer(), tabComplete.getCursor(), suggestions );
        bungee.getPluginManager().callEvent( tabCompleteEvent );

        if ( tabCompleteEvent.isCancelled() )
        {
            throw CancelSendSignal.INSTANCE;
        }

        List<String> results = tabCompleteEvent.getSuggestions();
        if ( !results.isEmpty() )
        {
            // Unclear how to handle 1.13 commands at this point. Because we don't inject into the command packets we are unlikely to get this far unless
            // Bungee plugins are adding results for commands they don't own anyway
            if ( con.getPendingConnection().getVersion() < ProtocolConstants.MINECRAFT_1_13 )
            {
                con.unsafe().sendPacket( new TabCompleteResponse( results ) );
            } else
            {
                int start = tabComplete.getCursor().lastIndexOf( ' ' ) + 1;
                int end = tabComplete.getCursor().length();
                StringRange range = StringRange.between( start, end );

                List<Suggestion> brigadier = new LinkedList<>();
                for ( String s : results )
                {
                    brigadier.add( new Suggestion( range, s ) );
                }

                con.unsafe().sendPacket( new TabCompleteResponse( tabComplete.getTransactionId(), new Suggestions( range, brigadier ) ) );
            }
            throw CancelSendSignal.INSTANCE;
        }

        // Don't forward tab completions if the command is a registered bungee command
        if ( isRegisteredCommand )
        {
            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(ClientSettings settings) throws Exception
    {
        con.setSettings( settings );

        SettingsChangedEvent settingsEvent = new SettingsChangedEvent( con );
        bungee.getPluginManager().callEvent( settingsEvent );
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception
    {
        if ( pluginMessage.getTag().equals( "BungeeCord" ) )
        {
            throw CancelSendSignal.INSTANCE;
        }

        PluginMessageEvent event = new PluginMessageEvent( con, con.getServer(), pluginMessage.getTag(), pluginMessage.getData().clone() );
        if ( bungee.getPluginManager().callEvent( event ).isCancelled() )
        {
            throw CancelSendSignal.INSTANCE;
        }

        con.getPendingConnection().relayMessage( pluginMessage );
    }

    @Override
    public String toString()
    {
        return "[" + con.getName() + "] -> UpstreamBridge";
    }
}