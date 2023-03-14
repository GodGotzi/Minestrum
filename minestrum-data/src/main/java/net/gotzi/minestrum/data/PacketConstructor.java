/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.data;

import net.gotzi.minestrum.data.packet.Packet;

import java.nio.ByteBuffer;

public interface PacketConstructor<P extends Packet> {

    P construct(ByteBuffer buffer);

}
