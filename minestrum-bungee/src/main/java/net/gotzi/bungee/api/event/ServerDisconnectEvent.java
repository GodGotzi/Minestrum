package net.gotzi.bungee.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.bungee.api.plugin.Event;
import net.gotzi.bungee.api.connection.ProxiedPlayer;

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