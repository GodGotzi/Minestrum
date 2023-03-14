/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.data.packet;

import net.gotzi.minestrum.data.PacketType;
import net.gotzi.minestrum.data.ServerType;

import java.nio.ByteBuffer;

public class StopPacket extends Packet<StopPacket> {

    public static StopPacket constructPacket(ByteBuffer buffer) {
        StopPacket stopPacket = new StopPacket();
        stopPacket.setPacketType(PacketType.Stop);
        stopPacket.constructDefault(buffer);

        return stopPacket;
    }

    public StopPacket() {

    }

    public StopPacket(ServerType serverType, int port, boolean backRequired) {
        setPacketType(PacketType.Stop);
        setServerType(serverType);
        setServerPort(port);
        setBackRequired(backRequired);
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}