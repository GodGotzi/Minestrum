/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.data.packet;

import net.gotzi.minestrum.data.PacketType;
import net.gotzi.minestrum.data.ServerType;

import java.nio.ByteBuffer;

public class InitServerPacket extends Packet<InitServerPacket> {

    public static InitServerPacket constructPacket(ByteBuffer buffer) {
        InitServerPacket initServerPacket = new InitServerPacket();
        initServerPacket.setPacketType(PacketType.InitServer);
        initServerPacket.constructDefault(buffer);

        return initServerPacket;
    }

    public InitServerPacket() {

    }

    public InitServerPacket(ServerType serverType, int port, boolean backRequired) {
        setPacketType(PacketType.InitServer);
        setServerType(serverType);
        setServerPort(port);
        setBackRequired(backRequired);
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
