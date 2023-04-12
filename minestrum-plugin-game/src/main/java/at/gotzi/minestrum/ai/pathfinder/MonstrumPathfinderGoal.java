package at.gotzi.minestrum.ai.pathfinder;

import at.gotzi.minestrum.ai.Monstrum;
import net.minecraft.server.v1_16_R3.*;

import java.util.Optional;
import java.util.stream.Stream;

public class MonstrumPathfinderGoal extends CustomPathfinderGoal<Monstrum> {

    private static int MAX_FOCUS_RANGE = 25;

    private int focusTick;

    private Stream<EntityPlayer> players;

    private Optional<EntityPlayer> focusPlayer;

    public MonstrumPathfinderGoal(Monstrum entity) {
        super(entity);
    }

    /*
    Go to location this.getEntity().getNavigation().a(0, 0, 0, 0);
     */

    @Override
    public void handle(Monstrum entity) {
        this.players = entity.getGamePlayers();

        Optional<EntityPlayer> target = playerInSight();

        if (target.isPresent()) {
            focus(target.get());
            this.focusTick = 0;
        }

        if (focusPlayer.isPresent()) {
            handleFocus(entity, focusPlayer.get());
            return;
        }


        entity.getNavigation().a(0, 0, 0, 0);
    }

    @Override
    public void handleAfter(Monstrum entity) {


        entity.damageEntity(DamageSource.mobAttack(entity), 10000);

    }

    public void focus(EntityPlayer entityPlayer) {
        this.focusPlayer = Optional.of(entityPlayer);




    }

    private void handleFocus(Monstrum entity, EntityPlayer entityPlayer) {
        if (checkIfOutOfFocusRange(entity, entityPlayer)) {
            focusPlayer = Optional.empty();
            focusTick = 0;
        } else if (focusTick >= 150) {
            focusPlayer = Optional.empty();
            focusTick = 0;
        } else {
            focusTick++;
        }
    }

    private boolean checkIfOutOfFocusRange(Monstrum entity, EntityPlayer entityPlayer) {
        if (entity.getBukkitEntity().getLocation().distance(entityPlayer.getBukkitEntity().getLocation()) >
                MAX_FOCUS_RANGE || (entity.isPlayerHiding(entityPlayer))) {
            return true;
        }

        return false;
    }

    public Optional<EntityPlayer> playerInSight() {


        return Optional.empty();
    }
}
