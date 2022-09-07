package net.gotzi.minestrum.data.packet;

import net.gotzi.minestrum.data.PacketType;
import net.gotzi.minestrum.data.ServerType;

import java.nio.ByteBuffer;

public class StatsPacket extends Packet<StatsPacket> {

    public static StatsPacket constructPacket(ByteBuffer buffer) {
        StatsPacket statsPacket = new StatsPacket();
        statsPacket.setPacketType(PacketType.Stats);
        statsPacket.constructDefault(buffer);

        return statsPacket;
    }

    public StatsPacket() {

    }

    public StatsPacket(ServerType serverType, int port, boolean backRequired) {
        setPacketType(PacketType.Stats);
        setServerType(serverType);
        setServerPort(port);
        setBackRequired(backRequired);
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
