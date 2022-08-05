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
public class PlayerListHeaderFooter extends DefinedPacket
{

    private String header;
    private String footer;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        header = readString( buf );
        footer = readString( buf );
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        writeString( header, buf );
        writeString( footer, buf );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
