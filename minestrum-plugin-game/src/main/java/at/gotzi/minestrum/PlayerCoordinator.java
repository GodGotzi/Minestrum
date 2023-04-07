package at.gotzi.minestrum;

import at.gotzi.minestrum.api.PlayerInfo;
import at.gotzi.minestrum.config.format.FormatType;
import at.gotzi.minestrum.config.format.FormatValue;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerCoordinator implements Listener {

    private final Game game;

    public PlayerCoordinator(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.game.registerPlayer(event.getPlayer());

        PlayerInfo playerInfo = this.game.getPlayerInfo(event.getPlayer());

        boolean full = true;//this.game.getStateHandler().isFull();

        if (!full) {
            event.setJoinMessage(game.getMessageConfig().getMessage(
                    "join.message", new FormatValue(FormatType.PLAYER, event.getPlayer().getName())
            ));

            Bukkit.getScheduler().runTaskLater(game, () -> {
                this.game.getStateHandler().start();
            }, 20);
        } else {
            event.setJoinMessage("");

        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerInfo playerInfo = this.game.getPlayerInfo(event.getPlayer());

        event.setQuitMessage(game.getMessageConfig().getMessage(
                "quit.message", new FormatValue(FormatType.PLAYER, event.getPlayer().getName())
        ));

        Bukkit.getScheduler().runTaskLater(game, () -> {
            this.game.getStateHandler().cancelCountdown();

            this.game.unregisterPlayer(event.getPlayer());
        }, 20);
    }
}
