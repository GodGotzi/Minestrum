package at.gotzi.minestrum.ai;

import at.gotzi.minestrum.Game;
import at.gotzi.minestrum.ai.pathfinder.MonstrumPathfinderGoal;
import at.gotzi.minestrum.api.GamePlayer;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class Monstrum extends EntityZombieHusk {

    private final Game game;

    public Monstrum(Game game, World world) {
        super(EntityTypes.HUSK, ((CraftWorld)world).getHandle());
        this.game = game;
        this.setInvulnerable(true);
        this.goalSelector.a(new PathfinderGoalLookAtPlayer(this, this.getClass(), 1));
        this.goalSelector.a(new PathfinderGoalRandomStroll(this, 2));
        this.goalSelector.a(new MonstrumPathfinderGoal(this));
    }

    public void killPlayer(Player player) {
        GamePlayer gamePlayer = this.game.getPlayerInfo(player);
    }

    public boolean isPlayerHiding(EntityPlayer entityPlayer) {
        GamePlayer gamePlayer = this.game.getPlayerInfo(entityPlayer.getBukkitEntity());
        return gamePlayer.isHiding();
    }

    public Stream<EntityPlayer> getGamePlayers() {
        return this.game.getStateHandler().getGamePlayers()
                .stream().map(gamePlayer -> ((CraftPlayer) gamePlayer.getNativePlayer()).getHandle());
    }

}
