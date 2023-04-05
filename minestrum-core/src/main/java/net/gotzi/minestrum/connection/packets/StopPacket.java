package net.gotzi.minestrum.connection.packets;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.communicate.receiver.Packet;
import net.gotzi.minestrum.server.Server;
import net.gotzi.minestrum.server.ServerHandler;

public class StopPacket extends Packet {
    public StopPacket(ServerHandler serverHandler) {
        super("stop", packetContext -> {
            String port = packetContext.channel().split(":")[1];
            int iPort = Integer.parseInt(port);
            Server server = serverHandler.getServerByPort(iPort);
            serverHandler.stopServer(server);
        });
    }
}
