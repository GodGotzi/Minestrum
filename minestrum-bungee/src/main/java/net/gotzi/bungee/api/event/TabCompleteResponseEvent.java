package net.gotzi.bungee.api.event;

import net.gotzi.bungee.api.connection.Connection;
import net.gotzi.bungee.api.plugin.Cancellable;

import java.util.List;

/**
 * Event called when a backend server sends a response to a player asking to
 * tab-complete a chat message or command. Note that this is not called when
 * BungeeCord or a plugin responds to a tab-complete request. Use
 * {@link TabCompleteEvent} for that.
 */
public class TabCompleteResponseEvent extends TargetedEvent implements Cancellable
{

    /**
     * Whether the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Mutable list of suggestions sent back to the player. If this list is
     * empty, an empty list is sent back to the client.
     */
    private final List<String> suggestions;

    public TabCompleteResponseEvent(Connection sender, Connection receiver, List<String> suggestions)
    {
        super( sender, receiver );
        this.suggestions = suggestions;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public List<String> getSuggestions() {
        return this.suggestions;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TabCompleteResponseEvent)) return false;
        final TabCompleteResponseEvent other = (TabCompleteResponseEvent) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        if (this.isCancelled() != other.isCancelled()) return false;
        final Object this$suggestions = this.getSuggestions();
        final Object other$suggestions = other.getSuggestions();
        if (this$suggestions == null ? other$suggestions != null : !this$suggestions.equals(other$suggestions))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TabCompleteResponseEvent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        result = result * PRIME + (this.isCancelled() ? 79 : 97);
        final Object $suggestions = this.getSuggestions();
        result = result * PRIME + ($suggestions == null ? 43 : $suggestions.hashCode());
        return result;
    }

    public String toString() {
        return "TabCompleteResponseEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", suggestions=" + this.getSuggestions() + ")";
    }
}
