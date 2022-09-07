package net.gotzi.minestrum.server.servers;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.server.Server;
import net.gotzi.minestrum.server.ServerHandler;
import net.gotzi.minestrum.server.ServerStartedFuture;

import java.io.IOException;

public class Lobby extends Server {

    public static Lobby startServer(ServerHandler handler) throws IOException {
        int port = handler.getNextFreePort();
        String cmd = handler.getProperties().getProperty("lobby.start");
        Process process = Runtime.getRuntime().exec(cmd);

        ServerStartedFuture serverStartedFuture = packet -> {
                Minestrum.getInstance().getLogger().log(LogLevel.FINE, " Lobby Server started -> {0}", packet);
        };

        return new Lobby(port, process, serverStartedFuture);
    }

    private Lobby(int portId, Process process, ServerStartedFuture serverStartedFuture) {
        super(portId, process, serverStartedFuture);
    }

}
