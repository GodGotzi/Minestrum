package net.gotzi.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gotzi.bungee.protocol.AbstractPacketHandler;
import net.gotzi.bungee.protocol.ProtocolConstants;
import net.gotzi.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BossBar extends DefinedPacket
{

    private UUID uuid;
    private int action;
    private String title;
    private float health;
    private int color;
    private int division;
    private byte flags;

    public BossBar(UUID uuid, int action)
    {
        this.uuid = uuid;
        this.action = action;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        uuid = readUUID( buf );
        action = readVarInt( buf );

        switch (action) {
            // Add
            case 0 -> {
                title = readString(buf);
                health = buf.readFloat();
                color = readVarInt(buf);
                division = readVarInt(buf);
                flags = buf.readByte();
            }
            // Health
            case 2 -> health = buf.readFloat();

            // Title
            case 3 -> title = readString(buf);

            // Style
            case 4 -> {
                color = readVarInt(buf);
                division = readVarInt(buf);
            }
            // Flags
            case 5 -> flags = buf.readByte();
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        writeUUID( uuid, buf );
        writeVarInt( action, buf );

        switch (action) {
            // Add
            case 0 -> {
                writeString(title, buf);
                buf.writeFloat(health);
                writeVarInt(color, buf);
                writeVarInt(division, buf);
                buf.writeByte(flags);
            }
            // Health
            case 2 -> buf.writeFloat(health);

            // Title
            case 3 -> writeString(title, buf);

            // Style
            case 4 -> {
                writeVarInt(color, buf);
                writeVarInt(division, buf);
            }
            // Flags
            case 5 -> buf.writeByte(flags);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
