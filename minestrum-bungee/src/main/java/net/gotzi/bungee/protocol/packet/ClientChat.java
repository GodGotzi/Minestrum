package net.gotzi.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gotzi.bungee.protocol.AbstractPacketHandler;
import net.gotzi.bungee.protocol.ProtocolConstants;
import net.gotzi.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClientChat extends DefinedPacket
{

    private String message;
    private long timestamp;
    private long salt;
    private byte[] signature;
    private boolean signedPreview;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        message = readString( buf, 256 );
        timestamp = buf.readLong();
        salt = buf.readLong();
        signature = readArray( buf );
        signedPreview = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        writeString( message, buf );
        buf.writeLong( timestamp );
        buf.writeLong( salt );
        writeArray( signature, buf );
        buf.writeBoolean( signedPreview );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
