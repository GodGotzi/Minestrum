package net.gotzi.minestrum.api.event;

import net.gotzi.minestrum.api.connection.Connection;
import net.gotzi.minestrum.api.plugin.Cancellable;

/**
 * Event called when a plugin message is sent to the client or server.
 */
public class PluginMessageEvent extends TargetedEvent implements Cancellable {

    /**
     * Cancelled state.
     */
    private boolean cancelled;
    /**
     * Tag specified for this plugin message.
     */
    private final String tag;
    /**
     * Data contained in this plugin message.
     */
    private final byte[] data;

    public PluginMessageEvent(Connection sender, Connection receiver, String tag, byte[] data)
    {
        super( sender, receiver );
        this.tag = tag;
        this.data = data;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public String getTag() {
        return this.tag;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PluginMessageEvent)) return false;
        final PluginMessageEvent other = (PluginMessageEvent) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        if (this.isCancelled() != other.isCancelled()) return false;
        final Object this$tag = this.getTag();
        final Object other$tag = other.getTag();
        if (this$tag == null ? other$tag != null : !this$tag.equals(other$tag)) return false;
        if (!java.util.Arrays.equals(this.getData(), other.getData())) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PluginMessageEvent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        result = result * PRIME + (this.isCancelled() ? 79 : 97);
        final Object $tag = this.getTag();
        result = result * PRIME + ($tag == null ? 43 : $tag.hashCode());
        result = result * PRIME + java.util.Arrays.hashCode(this.getData());
        return result;
    }

    public String toString() {
        return "PluginMessageEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", tag=" + this.getTag() + ")";
    }
}
