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
public class ClearTitles extends DefinedPacket
{

    private boolean reset;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        reset = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        buf.writeBoolean( reset );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
