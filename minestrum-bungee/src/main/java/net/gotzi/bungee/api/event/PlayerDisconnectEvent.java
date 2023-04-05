package net.gotzi.bungee.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.bungee.api.plugin.Event;
import net.gotzi.bungee.api.connection.ProxiedPlayer;

/**
 * Called when a player has left the proxy, it is not safe to call any methods
 * that perform an action on the passed player instance.
 */
@Data
@ToString()
@EqualsAndHashCode(callSuper = false)
public class PlayerDisconnectEvent extends Event
{

    /**
     * Player disconnecting.
     */
    private final ProxiedPlayer player;
}
