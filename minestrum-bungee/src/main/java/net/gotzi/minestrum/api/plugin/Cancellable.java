package net.gotzi.minestrum.api.plugin;

/**
 * Events that implement this indicate that they may be cancelled and thus
 * prevented from happening.
 */
public interface Cancellable
{

    /**
     * Get whether or not this event is cancelled.
     *
     * @return the cancelled state of this event
     */
    boolean isCancelled();

    /**
     * Sets the cancelled state of this event.
     *
     * @param cancel the state to set
     */
    void setCancelled(boolean cancel);
}
