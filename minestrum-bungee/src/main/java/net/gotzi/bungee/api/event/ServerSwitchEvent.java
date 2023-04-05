package net.gotzi.bungee.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.bungee.api.connection.ProxiedPlayer;
import net.gotzi.bungee.api.plugin.Event;

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
