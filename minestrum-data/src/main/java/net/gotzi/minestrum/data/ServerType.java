/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

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