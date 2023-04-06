package net.gotzi.minestrum.listener;

import net.gotzi.bungee.api.event.PlayerDisconnectEvent;
import net.gotzi.bungee.api.event.ServerConnectEvent;
import net.gotzi.bungee.api.plugin.Listener;
import net.gotzi.bungee.event.EventHandler;
import net.gotzi.minestrum.Minestrum;

public class PlayerConnectListener implements Listener {

    private final Minestrum minestrum;

    public PlayerConnectListener(Minestrum minestrum) {
        this.minestrum = minestrum;
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent connectEvent) {

    }

    @EventHandler
    public void onProxyExit(PlayerDisconnectEvent disconnectEvent) {

    }

}
