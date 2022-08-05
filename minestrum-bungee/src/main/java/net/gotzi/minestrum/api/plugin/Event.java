package net.gotzi.minestrum.api.plugin;

/**
 * Dummy class which all callable events must extend.
 */
public abstract class Event
{

    public Event() {
    }

    /**
     * Method called after this event has been dispatched to all handlers.
     */
    public void postCall()
    {
    }
}
