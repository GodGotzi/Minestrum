/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.communicate.receiver;

public interface PacketAction {

    void run(PacketContext packetContext);

}
