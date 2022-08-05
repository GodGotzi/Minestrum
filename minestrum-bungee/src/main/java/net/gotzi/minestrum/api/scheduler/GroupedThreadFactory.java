package net.gotzi.minestrum.api.scheduler;

import java.util.concurrent.ThreadFactory;
import lombok.Data;
import net.gotzi.minestrum.api.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

@Data
@Deprecated
public class GroupedThreadFactory implements ThreadFactory
{

    private final ThreadGroup group;

    public static final class BungeeGroup extends ThreadGroup
    {

        private BungeeGroup(String name)
        {
            super( name );
        }

    }

    public GroupedThreadFactory(Plugin plugin, String name)
    {
        this.group = new BungeeGroup( name );
    }

    @Override
    public Thread newThread(@NonNull Runnable r)
    {
        return new Thread( group, r );
    }
}
