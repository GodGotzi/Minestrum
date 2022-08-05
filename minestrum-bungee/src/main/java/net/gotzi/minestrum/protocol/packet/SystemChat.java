package net.gotzi.minestrum.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gotzi.minestrum.protocol.AbstractPacketHandler;
import net.gotzi.minestrum.protocol.DefinedPacket;
import net.gotzi.minestrum.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SystemChat extends DefinedPacket
{

    private String message;
    private int position;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        message = readString( buf, 262144 );
        position = readVarInt( buf );
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        writeString( message, buf );
        writeVarInt( position, buf );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
