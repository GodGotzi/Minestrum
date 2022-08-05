package net.gotzi.minestrum.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.minestrum.api.connection.PendingConnection;
import net.gotzi.minestrum.api.plugin.Event;
import net.gotzi.minestrum.protocol.packet.Handshake;

/**
 * Event called to represent a player first making their presence and username
 * known.
 */
@Data
@ToString()
@EqualsAndHashCode(callSuper = false)
public class PlayerHandshakeEvent extends Event
{

    /**
     * Connection attempting to login.
     */
    private final PendingConnection connection;
    /**
     * The handshake.
     */
    private final Handshake handshake;

    public PlayerHandshakeEvent(PendingConnection connection, Handshake handshake)
    {
        this.connection = connection;
        this.handshake = handshake;
    }
}
