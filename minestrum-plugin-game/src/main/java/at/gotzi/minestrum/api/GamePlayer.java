package at.gotzi.minestrum.api;

import org.bukkit.entity.Player;

public interface GamePlayer {

    boolean isGameSpectator();

    Player getNativePlayer();

    void killPlayer();

}
