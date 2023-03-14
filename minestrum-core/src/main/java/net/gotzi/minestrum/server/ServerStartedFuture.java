/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server;

import net.gotzi.minestrum.data.packet.InitServerPacket;

public interface ServerStartedFuture {

    void run(InitServerPacket initServerPacket);

}
