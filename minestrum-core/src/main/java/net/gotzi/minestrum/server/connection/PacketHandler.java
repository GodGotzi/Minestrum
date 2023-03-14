/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server.connection;

import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.data.packet.Packet;
import net.gotzi.minestrum.data.packet.InitServerPacket;
import net.gotzi.minestrum.data.packet.PlayerPacket;
import net.gotzi.minestrum.data.packet.StatsPacket;
import net.gotzi.minestrum.data.packet.StopPacket;
import net.gotzi.minestrum.data.ServerType;
import net.gotzi.minestrum.server.ServerStartedFuture;

import java.io.IOException;
import java.util.BitSet;
import java.util.logging.Logger;

public class PacketHandler {

    private final Logger logger;
    private final ServerSocketHub hub;

    public PacketHandler(ServerSocketHub hub, Logger logger) {
        this.logger = logger;
        this.hub = hub;
    }

    public void handle(Packet packet, SocketPipe pipe) {
        this.logger.log(LogLevel.INFO, "Packet received {}", packet.toString());

        switch (packet.getPacketType()) {
            case Player -> handle((PlayerPacket) packet);
            case Stats -> handle((StatsPacket) packet);
            case InitServer -> handle((InitServerPacket) packet, pipe);
            case Stop -> handle((StopPacket) packet);
        }

        if (packet.isBackRequired()) {
            try {
                pipe.write(packet);
                this.logger.log(LogLevel.INFO, "Packet send back {}", packet.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handle(PlayerPacket packet) {

    }

    public void handle(StatsPacket packet) {

    }

    public void handle(InitServerPacket packet) {
        if (packet.getServerType() == ServerType.LOBBY) {
            ServerStartedFuture serverStartedFuture = this.hub.getServerHandler()
                    .getServerByPort(packet.getServerPort()).getFuture();
            serverStartedFuture.run(packet);
        }
    }

    @SuppressWarnings("empty")
    public void handle(StopPacket packet) {}

}
