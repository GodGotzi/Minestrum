package net.gotzi.minestrum.listener;

import net.gotzi.bungee.api.event.PlayerDisconnectEvent;
import net.gotzi.bungee.api.event.PostLoginEvent;
import net.gotzi.bungee.api.event.ServerConnectEvent;
import net.gotzi.bungee.api.plugin.Listener;
import net.gotzi.bungee.event.EventHandler;
import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.server.Lobby;
import net.gotzi.minestrum.task.Task;

public class PlayerConnectListener implements Listener {

    private final Minestrum minestrum;

    public PlayerConnectListener(Minestrum minestrum) {
        this.minestrum = minestrum;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent loginEvent) {
        Lobby lobby = minestrum.getServerHandler().findNextLobby();
        loginEvent.getPlayer().connect(lobby.getServerInfo());

        minestrum.getTaskHandler().runDelayedTask(new Task("rearrange lobbies", () -> {
            minestrum.getServerHandler().rearrangeLobbyStructure();
        }), 1000);
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent connectEvent) {
        minestrum.getTaskHandler().runDelayedTask(new Task("rearrange lobbies", () -> {
            minestrum.getServerHandler().rearrangeLobbyStructure();
        }), 1000);
    }

    @EventHandler
    public void onProxyExit(PlayerDisconnectEvent disconnectEvent) {
        minestrum.getTaskHandler().runDelayedTask(new Task("rearrange lobbies", () -> {
            minestrum.getServerHandler().rearrangeLobbyStructure();
        }), 1000);
    }

}
