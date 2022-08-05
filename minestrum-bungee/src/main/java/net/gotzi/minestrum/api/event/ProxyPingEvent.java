package net.gotzi.minestrum.api.event;

import net.gotzi.minestrum.api.Callback;
import net.gotzi.minestrum.api.ServerPing;
import net.gotzi.minestrum.api.connection.PendingConnection;

/**
 * Called when the proxy is pinged with packet 0xFE from the server list.
 */
public class ProxyPingEvent extends AsyncEvent<ProxyPingEvent>
{

    /**
     * The connection asking for a ping response.
     */
    private final PendingConnection connection;
    /**
     * The data to respond with.
     */
    private ServerPing response;

    public ProxyPingEvent(PendingConnection connection, ServerPing response, Callback<ProxyPingEvent> done)
    {
        super( done );
        this.connection = connection;
        this.response = response;
    }

    public PendingConnection getConnection() {
        return this.connection;
    }

    public ServerPing getResponse() {
        return this.response;
    }

    public void setResponse(ServerPing response) {
        this.response = response;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ProxyPingEvent)) return false;
        final ProxyPingEvent other = (ProxyPingEvent) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$connection = this.getConnection();
        final Object other$connection = other.getConnection();
        if (this$connection == null ? other$connection != null : !this$connection.equals(other$connection))
            return false;
        final Object this$response = this.getResponse();
        final Object other$response = other.getResponse();
        if (this$response == null ? other$response != null : !this$response.equals(other$response)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ProxyPingEvent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $connection = this.getConnection();
        result = result * PRIME + ($connection == null ? 43 : $connection.hashCode());
        final Object $response = this.getResponse();
        result = result * PRIME + ($response == null ? 43 : $response.hashCode());
        return result;
    }

    public String toString() {
        return "ProxyPingEvent(connection=" + this.getConnection() + ", response=" + this.getResponse() + ")";
    }
}
