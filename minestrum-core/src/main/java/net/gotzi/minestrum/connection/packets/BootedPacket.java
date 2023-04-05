package net.gotzi.minestrum.connection.packets;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.communicate.receiver.Packet;
import net.gotzi.minestrum.server.ServerHandler;

public class BootedPacket extends Packet {

    public BootedPacket(ServerHandler serverHandler) {
        super("booted", packetContext -> {
            String port = packetContext.channel().split(":")[1];
            int iPort = Integer.parseInt(port);
            serverHandler.getServerByPort(iPort).booted();

            Minestrum.getInstance().getLogger().log(LogLevel.FINE, "Server " + port + " booted!");
        });
    }

}
