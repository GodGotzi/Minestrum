package at.gotzi.minestrum.state;

import at.gotzi.minestrum.Game;
import at.gotzi.minestrum.config.format.FormatType;
import at.gotzi.minestrum.config.format.FormatValue;
import at.gotzi.minestrum.spectator.SpectatorHandler;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GameStateHandler {

    private final Game game;
    private final SpectatorHandler spectatorHandler;

    @Getter
    private Countdown countdown;

    @Getter
    private PrimaryGameState primaryGameState;

    public GameStateHandler(PrimaryGameState gameState, Game game) {
        this.primaryGameState = gameState;
        this.game = game;
        this.spectatorHandler = game.getSpectatorHandler();
    }

    public void start() {
        this.primaryGameState = PrimaryGameState.STARTING;

        FormatValue<Integer> formatValue = new FormatValue<>(FormatType.COUNTER);

        this.countdown = new Countdown(30, this.game) {
            @Override
            public void count(int current) {
                formatValue.setFormat(current);

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
                    primaryGameState = PrimaryGameState.PLAYING;
                }
            }
        };
    }

    public void broadcastMessage(String msg) {
        Arrays.stream(msg.split("$")).forEach(Bukkit::broadcastMessage);
    }

    /*
    public boolean isFull() {
        Object objAmount = ((DedicatedServer) MinecraftServer.getServer()).getDedicatedServerProperties().properties.get("max-game-players");
        int amount = Integer.parseInt(String.valueOf(objAmount));

        if (amount <= players.size())
            return true;

        return false;
    }

    public void quitPlayer(Player player) {
        if (players.contains(player)) {
            if (players.size() == 1) {
                players.clear();

                if (this.primaryGameState == PrimaryGameState.PLAYING) {
                    this.primaryGameState = PrimaryGameState.ENDING;

                    broadcastMessage(game.getMessageConfig().getMessage("game.cancel"));
                } else if (this.primaryGameState == PrimaryGameState.STARTING)
                    this.primaryGameState = PrimaryGameState.IDLE;
            }
        }
    }
    */

    public void cancelCountdown() {
        countdown.stop();
    }

}
