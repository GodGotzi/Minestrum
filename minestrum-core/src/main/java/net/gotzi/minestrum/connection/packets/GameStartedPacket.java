package net.gotzi.minestrum.connection.packets;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.SyncFuture;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.communicate.receiver.Packet;
import net.gotzi.minestrum.server.PlayServer;
import net.gotzi.minestrum.server.ServerHandler;

public class GameStartedPacket extends Packet {
    public GameStartedPacket(ServerHandler serverHandler) {
        super("gamestarted", packetContext -> {
            String port = packetContext.channel().split(":")[1];
            int iPort = Integer.parseInt(port);
            serverHandler.nextServer(new SyncFuture<>() {
                @Override
                public void done(PlayServer playServer) {}
            }, serverHandler.getServerByPort(iPort).getMaxPlayers());

            Minestrum.getInstance().getLogger().log(LogLevel.FINE, "Game " + port + " started!");
        });
    }
}
