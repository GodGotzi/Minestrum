package at.gotzi.minestrum.state;

import at.gotzi.minestrum.Game;
import at.gotzi.minestrum.api.GamePlayer;
import at.gotzi.minestrum.api.SizedList;
import at.gotzi.minestrum.config.format.FormatType;
import at.gotzi.minestrum.config.format.FormatValue;
import at.gotzi.minestrum.listener.player.PlayerListenerAdapter;
import at.gotzi.minestrum.spectator.SpectatorRegistry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class GameStateHandler extends PlayerListenerAdapter implements Listener {

    @Getter
    private SizedList<GamePlayer> gamePlayers;

    private final Game game;
    private final SpectatorRegistry spectatorRegistry;

    @Getter
    private GameState gameState;

    public GameStateHandler(GameState gameState, Game game) {
        this.gamePlayers = new SizedList<>(new GamePlayer[game.getMaxGamePlayers()]);
        this.gameState = gameState;
        this.game = game;
        this.spectatorRegistry = game.getSpectatorRegistry();
    }

    public void start() {
        this.gameState = GameState.STARTING;

        FormatValue<Integer> formatValue = new FormatValue<>(FormatType.COUNTER);

        GameCountdown gameCountdown = new GameCountdown(30, this.game) {
            @Override
            public void count(int current) {
                formatValue.setFormat(current);

                if (gameState != GameState.STARTING) this.stop();

                if (current == 30) {
                    broadcastMessage(game.getMessageConfig().getMessage("countdown.start.msg", formatValue));
                    broadcastMessage(game.getMessageConfig().getMessage("countdown.running.msg", formatValue));
                } else if (current == 15) {
                    broadcastMessage(game.getMessageConfig().getMessage("countdown.running.msg", formatValue));
                } else if (current == 10) {
                    broadcastMessage(game.getMessageConfig().getMessage("countdown.running.msg", formatValue));
                } else if (current <= 5 && current >= 1) {
                    broadcastMessage(game.getMessageConfig().getMessage("countdown.running.msg", formatValue));
                } else if (current == 0) {
                    broadcastMessage(game.getMessageConfig().getMessage("countdown.end.msg", formatValue));
                    gameState = GameState.PLAYING;
                }
            }
        };

        gameCountdown.start();
    }

    public void broadcastMessage(String msg) {
        Arrays.stream(msg.split("$")).forEach(Bukkit::broadcastMessage);
    }

    public void cancelCountdown() {
        if (this.gameState == GameState.STARTING) {
            this.gameState = GameState.IDLE;
        }
    }

    @Override
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = this.game.getPlayerInfo(player);
        int maxPlayers = this.game.getMaxGamePlayers();

        if (maxPlayers < gamePlayers.size()) {
            gamePlayers.add(gamePlayer);


        } else {
            this.spectatorRegistry.register(player.getUniqueId());
        }

    }

    @Override
    public void onQuit(PlayerQuitEvent event) {






    }

    @Override
    public void onLogin(PlayerLoginEvent event) {
        super.onLogin(event);
    }

    public void startGame() {
        this.gameState = GameState.ENDING;
    }

    public void finished() {
        this.gameState = GameState.ENDING;
    }

    public int getPlayerAmount() {
        return gamePlayers.size();
    }
}
