package net.gotzi.minestrum.server;

import net.gotzi.minestrum.server.Server;
import net.gotzi.minestrum.server.ServerStartedFuture;

import java.io.File;

public class PlayServer extends Server {

    public PlayServer(int maxPlayers, File sourceFolder, File destinationFolder, int portId, ServerStartedFuture serverStartedFuture) {
        super(maxPlayers, sourceFolder, destinationFolder, portId, serverStartedFuture);
    }

    @Override
    public boolean isLobby() {
        return false;
    }

}
