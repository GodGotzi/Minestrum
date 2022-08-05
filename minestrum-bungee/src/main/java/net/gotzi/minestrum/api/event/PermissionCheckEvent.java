package net.gotzi.minestrum.api.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.gotzi.minestrum.api.plugin.Event;
import net.gotzi.minestrum.api.CommandSender;

/**
 * Called when the permission of a CommandSender is checked.
 */
@Data
@AllArgsConstructor
@ToString()
@EqualsAndHashCode(callSuper = false)
public class PermissionCheckEvent extends Event
{

    /**
     * The command sender being checked for a permission.
     */
    private final CommandSender sender;
    /**
     * The permission to check.
     */
    private final String permission;
    /**
     * The outcome of this permission check.
     */
    @Getter(AccessLevel.NONE)
    private boolean hasPermission;

    public boolean hasPermission() {
        return hasPermission;
    }
}
