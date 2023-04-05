package net.gotzi.bungee.api.event;

import java.net.SocketAddress;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.bungee.api.plugin.Event;
import net.gotzi.bungee.api.config.ListenerInfo;
import net.gotzi.bungee.api.plugin.Cancellable;

/**
 * Event called to represent an initial client connection.
 * <br>
 * Note: This event is called at an early stage of every connection, handling
 * should be <b>fast</b>.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class ClientConnectEvent extends Event implements Cancellable
{

    /**
     * Cancelled state.
     */
    private boolean cancelled;
    /**
     * Remote address of connection.
     */
    private final SocketAddress socketAddress;
    /**
     * Listener that accepted the connection.
     */
    private final ListenerInfo listener;
}
