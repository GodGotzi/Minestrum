/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server;

import java.io.File;
import java.util.Properties;

public class Lobby extends Server {

    public Lobby(File sourceFolder, File destinationFolder, int portId, ServerStartedFuture serverStartedFuture) {
        super(sourceFolder, destinationFolder, portId, serverStartedFuture);

        setName("lobby-" + portId);
    }

    @Override
    public void editProperties(Properties properties) { }

    @Override
    public boolean isLobby() {
        return true;
    }


}
