package net.gotzi.minestrum.server;

import net.gotzi.minestrum.server.Server;
import net.gotzi.minestrum.server.ServerStartedFuture;

import java.io.File;

public class PlayServer extends Server {

    public PlayServer(File sourceFolder, File destinationFolder, int ramMB, int portId, ServerStartedFuture serverStartedFuture) {
        super(sourceFolder, destinationFolder, ramMB, portId, serverStartedFuture);
    }

    @Override
    public boolean isLobby() {
        return false;
    }

}
