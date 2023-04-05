package net.gotzi.bungee.util.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.gotzi.bungee.api.plugin.Plugin;
import net.gotzi.bungee.api.scheduler.ScheduledTask;
import net.gotzi.bungee.api.scheduler.TaskScheduler;

public class BungeeScheduler implements TaskScheduler
{

    private final Object lock = new Object();
    private final AtomicInteger taskCounter = new AtomicInteger();
    private final TIntObjectMap<BungeeTask> tasks = TCollections.synchronizedMap(new TIntObjectHashMap<>() );
    private final Multimap<Plugin, BungeeTask> tasksByPlugin = Multimaps.synchronizedMultimap( HashMultimap.<Plugin, BungeeTask>create() );
    //
    private final Unsafe unsafe = Plugin::getExecutorService;

    @Override
    public void cancel(int id)
    {
        BungeeTask task = tasks.get( id );
        Preconditions.checkArgument( task != null, "No task with id %s", id );

        task.cancel();
    }

    void cancel0(BungeeTask task)
    {
        synchronized ( lock )
        {
            tasks.remove( task.getId() );
            tasksByPlugin.values().remove( task );
        }
    }

    @Override
    public void cancel(ScheduledTask task)
    {
        task.cancel();
    }

    @Override
    public int cancel(Plugin plugin)
    {
        Set<ScheduledTask> toRemove;
        synchronized ( lock )
        {
            toRemove = new HashSet<>(tasksByPlugin.get(plugin));
        }
        for ( ScheduledTask task : toRemove )
        {
            cancel( task );
        }
        return toRemove.size();
    }

    @Override
    public ScheduledTask runAsync(Plugin owner, Runnable task)
    {
        return schedule( owner, task, 0, TimeUnit.MILLISECONDS );
    }

    @Override
    public ScheduledTask schedule(Plugin owner, Runnable task, long delay, TimeUnit unit) {
        return schedule( owner, task, delay, 0, unit );
    }

    @Override
    public ScheduledTask schedule(Plugin owner, Runnable task, long delay, long period, TimeUnit unit)
    {
        Preconditions.checkNotNull( owner, "owner" );
        Preconditions.checkNotNull( task, "task" );
        BungeeTask prepared = new BungeeTask( this, taskCounter.getAndIncrement(), owner, task, delay, period, unit );

        synchronized ( lock )
        {
            tasks.put( prepared.getId(), prepared );
            tasksByPlugin.put( owner, prepared );
        }

        owner.getExecutorService().execute( prepared );
        return prepared;
    }

    @Override
    public Unsafe unsafe()
    {
        return unsafe;
    }
}
