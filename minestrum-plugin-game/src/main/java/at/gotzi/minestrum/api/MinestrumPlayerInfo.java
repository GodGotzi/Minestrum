package at.gotzi.minestrum.api;

import at.gotzi.minestrum.Game;
import org.bukkit.entity.Player;

public class MinestrumPlayerInfo implements PlayerInfo {

    private final Game game;
    private final Player player;

    public MinestrumPlayerInfo(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public boolean isGameSpectator() {

        //TODO
        return true;
    }

    @Override
    public void quitGame() {

    }
}
