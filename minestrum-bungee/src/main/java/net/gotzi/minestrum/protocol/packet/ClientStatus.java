package net.gotzi.minestrum.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gotzi.minestrum.protocol.AbstractPacketHandler;
import net.gotzi.minestrum.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClientStatus extends DefinedPacket
{

    private byte payload;

    @Override
    public void read(ByteBuf buf)
    {
        payload = buf.readByte();
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeByte( payload );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
