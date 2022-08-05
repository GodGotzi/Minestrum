package net.gotzi.minestrum.api.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.gotzi.minestrum.api.plugin.Event;
import net.gotzi.minestrum.api.CommandSender;

/**
 * Called when somebody reloads BungeeCord
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProxyReloadEvent extends Event
{

    /**
     * Creator of the action.
     */
    private final CommandSender sender;
}
