package net.gotzi.minestrum.data;

public enum ServerType {

    PROXY,
    LOBBY,
    SOLO,
    MULTI;

    public static int getLobbyPort() {
        return 25580;
    }
}