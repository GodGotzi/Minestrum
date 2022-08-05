package net.gotzi.minestrum.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gotzi.minestrum.protocol.AbstractPacketHandler;
import net.gotzi.minestrum.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StatusRequest extends DefinedPacket
{

    @Override
    public void read(ByteBuf buf)
    {
    }

    @Override
    public void write(ByteBuf buf)
    {
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
