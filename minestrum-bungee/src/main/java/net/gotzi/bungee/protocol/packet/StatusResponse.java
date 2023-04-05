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
public class StatusResponse extends DefinedPacket
{

    private String response;

    @Override
    public void read(ByteBuf buf)
    {
        response = readString( buf );
    }

    @Override
    public void write(ByteBuf buf)
    {
        writeString( response, buf );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
