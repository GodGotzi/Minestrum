package at.gotzi.minestrum.ai.pathfinder;

import lombok.Getter;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;

public abstract class CustomPathfinderGoal<T extends EntityLiving> extends PathfinderGoal {

    @Getter
    private T entity;

    public CustomPathfinderGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean a() {
        handle(this.entity);
        return true;
    }

    @Override
    public void c() {
        handleAfter(this.entity);
    }

    public abstract void handle(T entity);
    public abstract void handleAfter(T entity);
}
