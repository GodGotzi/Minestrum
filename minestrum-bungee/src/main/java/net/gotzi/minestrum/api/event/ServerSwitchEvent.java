package net.gotzi.minestrum.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.minestrum.api.config.ServerInfo;
import net.gotzi.minestrum.api.connection.ProxiedPlayer;
import net.gotzi.minestrum.api.plugin.Event;

/**
 * Called when a player has changed servers.
 */
@Data
@ToString()
@EqualsAndHashCode(callSuper = false)
public class ServerSwitchEvent extends Event
{

    /**
     * Player whom the server is for.
     */
    private final ProxiedPlayer player;
    /**
     * Server the player is switch from. May be null if initial proxy
     * connection.
     */
    private final ServerInfo from;
}
