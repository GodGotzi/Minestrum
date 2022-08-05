package net.gotzi.minestrum.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gotzi.minestrum.protocol.AbstractPacketHandler;
import net.gotzi.minestrum.protocol.DefinedPacket;
import net.gotzi.minestrum.protocol.OverflowPacketException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoginPayloadRequest extends DefinedPacket
{

    private int id;
    private String channel;
    private byte[] data;

    @Override
    public void read(ByteBuf buf)
    {
        id = readVarInt( buf );
        channel = readString( buf );

        int len = buf.readableBytes();
        if ( len > 1048576 )
        {
            throw new OverflowPacketException( "Payload may not be larger than 1048576 bytes" );
        }
        data = new byte[ len ];
        buf.readBytes( data );
    }

    @Override
    public void write(ByteBuf buf)
    {
        writeVarInt( id, buf );
        writeString( channel, buf );
        buf.writeBytes( data );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
