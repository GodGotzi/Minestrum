/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server;

import java.io.File;

public class Lobby extends Server {

    public Lobby(int maxPlayers, File sourceFolder, File destinationFolder, int portId, ServerStartedFuture serverStartedFuture) {
        super(maxPlayers, sourceFolder, destinationFolder, portId, serverStartedFuture);

        setName("lobby-" + portId);
    }

    @Override
    public boolean isLobby() {
        return true;
    }


}
