package net.gotzi.minestrum.server;

import lombok.Getter;
import net.gotzi.minestrum.server.Server;
import net.gotzi.minestrum.server.ServerStartedFuture;

import java.io.File;
import java.util.Properties;

public class PlayServer extends Server {

    @Getter
    private final int maxPlayers;

    public PlayServer(int maxPlayers, File sourceFolder, File destinationFolder, int portId, ServerStartedFuture serverStartedFuture) {
        super(sourceFolder, destinationFolder, portId, serverStartedFuture);

        this.maxPlayers = maxPlayers;
    }

    @Override
    public void editProperties(Properties properties) {
        properties.setProperty("max-game-players", String.valueOf(maxPlayers));
    }

    @Override
    public boolean isLobby() {
        return false;
    }

}
