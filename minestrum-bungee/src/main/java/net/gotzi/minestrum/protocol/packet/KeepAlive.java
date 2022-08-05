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
public class KeepAlive extends DefinedPacket
{

    private long randomId;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        randomId = ( protocolVersion >= ProtocolConstants.MINECRAFT_1_12_2 ) ? buf.readLong() : readVarInt( buf );
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_12_2 )
        {
            buf.writeLong( randomId );
        } else
        {
            writeVarInt( (int) randomId, buf );
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
