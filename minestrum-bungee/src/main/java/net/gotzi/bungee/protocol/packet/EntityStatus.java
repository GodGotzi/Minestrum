package net.gotzi.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gotzi.bungee.protocol.AbstractPacketHandler;
import net.gotzi.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EntityStatus extends DefinedPacket
{

    public static final byte DEBUG_INFO_REDUCED = 22;
    public static final byte DEBUG_INFO_NORMAL = 23;
    //
    private int entityId;
    private byte status;

    @Override
    public void read(ByteBuf buf)
    {
        entityId = buf.readInt();
        status = buf.readByte();
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeInt( entityId );
        buf.writeByte( status );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
