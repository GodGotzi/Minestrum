/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.data;

import java.util.Properties;

public enum ServerType {
    PROXY,
    LOBBY,
    PLAY;

    public static int getLobbyPort() {
        return 25580;
    }

    public static String getSourceFolder(ServerType serverType, Properties properties) {
        return switch (serverType) {
            case PROXY -> "";
            case LOBBY -> properties.getProperty("servers.source.lobby.dir");
            case PLAY -> properties.getProperty("servers.source.play.dir");
        };
    }

    public static String getDestinationFolder(ServerType serverType, Properties properties) {
        return switch (serverType) {
            case PROXY -> "";
            case LOBBY -> properties.getProperty("servers.dest.lobby.dir");
            case PLAY -> properties.getProperty("servers.dest.play.dir");
        };
    }


}