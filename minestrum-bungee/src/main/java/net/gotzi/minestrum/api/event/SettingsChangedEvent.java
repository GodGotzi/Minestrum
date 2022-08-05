package net.gotzi.minestrum.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.minestrum.api.connection.ProxiedPlayer;
import net.gotzi.minestrum.api.plugin.Event;

/**
 * Called after a {@link ProxiedPlayer} changed one or more of the following
 * (client-side) settings:
 *
 * <ul>
 * <li>View distance</li>
 * <li>Locale</li>
 * <li>Displayed skin parts</li>
 * <li>Chat visibility</li>
 * <li>Chat colors</li>
 * <li>Main hand side (left or right)</li>
 * </ul>
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class SettingsChangedEvent extends Event
{

    /**
     * Player who changed the settings.
     */
    private final ProxiedPlayer player;
}
