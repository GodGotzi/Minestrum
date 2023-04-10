package at.gotzi.minestrum.listener.player;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class PlayerListenerAdapter {

    public void onJoin(PlayerJoinEvent event) { }

    public void onQuit(PlayerQuitEvent event) { }

    public void onLogin(PlayerLoginEvent event) { }

}
