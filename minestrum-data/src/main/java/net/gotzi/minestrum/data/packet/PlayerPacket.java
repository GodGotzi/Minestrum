/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.data.packet;

import net.gotzi.minestrum.data.PacketType;
import net.gotzi.minestrum.data.ServerType;

import java.nio.ByteBuffer;

public class PlayerPacket extends Packet<PlayerPacket> {

    public static PlayerPacket constructPacket(ByteBuffer buffer) {
        PlayerPacket playerPacket = new PlayerPacket();
        playerPacket.setPacketType(PacketType.Player);
        playerPacket.constructDefault(buffer);

        return playerPacket;
    }

    public PlayerPacket() {

    }

    public PlayerPacket(ServerType serverType, int port, boolean backRequired) {
        setPacketType(PacketType.Player);
        setServerType(serverType);
        setServerPort(port);
        setBackRequired(backRequired);
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

}
