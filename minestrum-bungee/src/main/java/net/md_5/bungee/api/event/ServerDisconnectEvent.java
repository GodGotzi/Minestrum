package net.md_5.bungee.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

@Data
@AllArgsConstructor
@ToString(callSuper = false)
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