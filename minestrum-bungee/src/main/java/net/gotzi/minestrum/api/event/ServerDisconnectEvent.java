package net.gotzi.minestrum.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.minestrum.api.config.ServerInfo;
import net.gotzi.minestrum.api.connection.ProxiedPlayer;
import net.gotzi.minestrum.api.plugin.Event;

@Data
@AllArgsConstructor
@ToString()
@EqualsAndHashCode(callSuper = false)
public class ServerDisconnectEvent extends Event
{

    /**
     * Player disconnecting from a server.
     */
    private final ProxiedPlayer player;
    /**
     * Server the player is disconnecting from.
     */
    private final ServerInfo target;
}