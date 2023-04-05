package net.gotzi.bungee.api.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.gotzi.bungee.api.CommandSender;
import net.gotzi.bungee.api.plugin.Event;

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
