/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.data;

import net.gotzi.minestrum.data.packet.InitServerPacket;
import net.gotzi.minestrum.data.packet.PlayerPacket;
import net.gotzi.minestrum.data.packet.StatsPacket;
import net.gotzi.minestrum.data.packet.StopPacket;

public enum PacketType {

    Player(10, PlayerPacket::constructPacket),
    Stats(10, StatsPacket::constructPacket),
    Stop(10, StopPacket::constructPacket),
    InitServer(10, InitServerPacket::constructPacket);

    private final int size;
    private final PacketConstructor<?> constructor;

    PacketType(int size, PacketConstructor<?> constructor) {
        this.size = size;
        this.constructor = constructor;
    }

    public int size() {
        return size;
    }

    public PacketConstructor<?> getConstructor() {
        return constructor;
    }

    public static PacketType getById(int id) {
        return values()[id];
    }
}