package net.gotzi.minestrum.api.event;

import net.gotzi.minestrum.api.Callback;
import net.gotzi.minestrum.api.chat.BaseComponent;
import net.gotzi.minestrum.api.chat.TextComponent;
import net.gotzi.minestrum.api.connection.PendingConnection;
import net.gotzi.minestrum.api.plugin.Cancellable;

/**
 * Event called to represent a player logging in.
 */
public class LoginEvent extends AsyncEvent<LoginEvent> implements Cancellable
{

    /**
     * Cancelled state.
     */
    private boolean cancelled;
    /**
     * Message to use when kicking if this event is canceled.
     */
    private BaseComponent[] cancelReasonComponents;
    /**
     * Connection attempting to login.
     */
    private final PendingConnection connection;

    public LoginEvent(PendingConnection connection, Callback<LoginEvent> done)
    {
        super( done );
        this.connection = connection;
    }

    /**
     * @return reason to be displayed
     * @deprecated Use component methods instead.
     */
    @Deprecated
    public String getCancelReason()
    {
        return BaseComponent.toLegacyText( getCancelReasonComponents() );
    }

    /**
     * @param cancelReason reason to be displayed
     * @deprecated Use
     * {@link #setCancelReason(BaseComponent...)}
     * instead.
     */
    @Deprecated
    public void setCancelReason(String cancelReason)
    {
        setCancelReason( TextComponent.fromLegacyText( cancelReason ) );
    }

    public void setCancelReason(BaseComponent... cancelReason)
    {
        this.cancelReasonComponents = cancelReason;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public BaseComponent[] getCancelReasonComponents() {
        return this.cancelReasonComponents;
    }

    public PendingConnection getConnection() {
        return this.connection;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof LoginEvent)) return false;
        final LoginEvent other = (LoginEvent) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isCancelled() != other.isCancelled()) return false;
        if (!java.util.Arrays.deepEquals(this.getCancelReasonComponents(), other.getCancelReasonComponents()))
            return false;
        final Object this$connection = this.getConnection();
        final Object other$connection = other.getConnection();
        if (this$connection == null ? other$connection != null : !this$connection.equals(other$connection))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof LoginEvent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isCancelled() ? 79 : 97);
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getCancelReasonComponents());
        final Object $connection = this.getConnection();
        result = result * PRIME + ($connection == null ? 43 : $connection.hashCode());
        return result;
    }

    public String toString() {
        return "LoginEvent(cancelled=" + this.isCancelled() + ", cancelReasonComponents=" + java.util.Arrays.deepToString(this.getCancelReasonComponents()) + ", connection=" + this.getConnection() + ")";
    }
}
