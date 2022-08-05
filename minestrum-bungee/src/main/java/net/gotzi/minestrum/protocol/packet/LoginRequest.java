package net.gotzi.minestrum.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gotzi.minestrum.protocol.AbstractPacketHandler;
import net.gotzi.minestrum.protocol.DefinedPacket;
import net.gotzi.minestrum.protocol.PlayerPublicKey;
import net.gotzi.minestrum.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoginRequest extends DefinedPacket
{

    private String data;
    private PlayerPublicKey publicKey;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        data = readString( buf, 16 );
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19 )
        {
            publicKey = readPublicKey( buf );
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        writeString( data, buf );
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19 )
        {
            writePublicKey( publicKey, buf );
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
