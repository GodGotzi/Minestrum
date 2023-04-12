package at.gotzi.minestrum.ai.pathfinder;

import at.gotzi.minestrum.ai.Monstrum;
import net.minecraft.server.v1_16_R3.*;

import java.util.stream.Stream;

public class MonstrumPathfinderGoal extends CustomPathfinderGoal<Monstrum> {

    private Stream<EntityPlayer> players;

    public MonstrumPathfinderGoal(Monstrum entity) {
        super(entity);


    }

    /*
    Go to location this.getEntity().getNavigation().a(0, 0, 0, 0);


     */

    @Override
    public void handle(Monstrum entity) {
        this.players = entity.getGamePlayers();




        entity.getNavigation().a(0, 0, 0, 0);
    }

    @Override
    public void handleAfter(Monstrum entity) {


        .damageEntity(DamageSource.mobAttack(entity), 10000);

    }
}
