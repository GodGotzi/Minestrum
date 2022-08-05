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
public class ScoreboardScore extends DefinedPacket
{

    private String itemName;
    /**
     * 0 = create / update, 1 = remove.
     */
    private byte action;
    private String scoreName;
    private int value;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        itemName = readString( buf );
        action = buf.readByte();
        scoreName = readString( buf );
        if ( action != 1 )
        {
            value = readVarInt( buf );
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        writeString( itemName, buf );
        buf.writeByte( action );
        writeString( scoreName, buf );
        if ( action != 1 )
        {
            writeVarInt( value, buf );
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
