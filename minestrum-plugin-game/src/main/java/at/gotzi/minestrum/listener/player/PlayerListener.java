package at.gotzi.minestrum.listener.player;

import at.gotzi.minestrum.listener.AbstractListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener extends AbstractListener<PlayerListenerAdapter> implements Listener {

    private List<PlayerListenerAdapter> playerListenerAdapters;

    public PlayerListener() {
        super(new ArrayList<>());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.playerListenerAdapters.forEach(adapter -> adapter.onJoin(event));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.playerListenerAdapters.forEach(adapter -> adapter.onQuit(event));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        this.playerListenerAdapters.forEach(adapter -> adapter.onLogin(event));
    }

}
