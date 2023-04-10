package at.gotzi.minestrum.api;

import at.gotzi.minestrum.Game;
import org.bukkit.entity.Player;

public class MinestrumGamePlayer implements GamePlayer {

    private final Game game;
    private final Player player;

    public MinestrumGamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public boolean isGameSpectator() {
        return this.game.getSpectatorRegistry().isGameSpectator(player.getUniqueId());
    }

    @Override
    public Player getNativePlayer() {
        return this.player;
    }

}
