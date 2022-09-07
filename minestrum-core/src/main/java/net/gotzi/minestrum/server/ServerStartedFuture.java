package net.gotzi.minestrum.server;

import net.gotzi.minestrum.data.packet.InitServerPacket;

public interface ServerStartedFuture {

    void run(InitServerPacket initServerPacket);

}
