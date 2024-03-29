package net.gotzi.bungee.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.bungee.api.connection.ProxiedPlayer;
import net.gotzi.bungee.api.plugin.Event;
import net.gotzi.bungee.api.plugin.Cancellable;
import net.gotzi.bungee.api.chat.BaseComponent;
import net.gotzi.bungee.api.chat.TextComponent;

/**
 * Represents a player getting kicked from a server.
 */
@Data
@ToString()
@EqualsAndHashCode(callSuper = false)
public class ServerKickEvent extends Event implements Cancellable
{

    /**
     * Cancelled status.
     */
    private boolean cancelled;
    /**
     * Player being kicked.
     */
    private final ProxiedPlayer player;
    /**
     * The server the player was kicked from, should be used in preference to
     * {@link ProxiedPlayer#getServer()}.
     */
    private final ServerInfo kickedFrom;
    /**
     * Kick reason.
     */
    private BaseComponent[] kickReasonComponent;
    /**
     * Server to send player to if this event is cancelled.
     */
    private ServerInfo cancelServer;
    /**
     * State in which the kick occured.
     */
    private State state;

    public enum State
    {

        CONNECTING, CONNECTED, UNKNOWN
    }

    @Deprecated
    public ServerKickEvent(ProxiedPlayer player, BaseComponent[] kickReasonComponent, ServerInfo cancelServer)
    {
        this( player, kickReasonComponent, cancelServer, State.UNKNOWN );
    }

    @Deprecated
    public ServerKickEvent(ProxiedPlayer player, BaseComponent[] kickReasonComponent, ServerInfo cancelServer, State state)
    {
        this( player, player.getServer().getInfo(), kickReasonComponent, cancelServer, state );
    }

    public ServerKickEvent(ProxiedPlayer player, ServerInfo kickedFrom, BaseComponent[] kickReasonComponent, ServerInfo cancelServer, State state)
    {
        this.player = player;
        this.kickedFrom = kickedFrom;
        this.kickReasonComponent = kickReasonComponent;
        this.cancelServer = cancelServer;
        this.state = state;
    }

    @Deprecated
    public String getKickReason()
    {
        return BaseComponent.toLegacyText( kickReasonComponent );
    }

    @Deprecated
    public void setKickReason(String reason)
    {
        kickReasonComponent = TextComponent.fromLegacyText( reason );
    }
}
