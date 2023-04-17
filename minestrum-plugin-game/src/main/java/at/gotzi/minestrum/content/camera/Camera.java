package at.gotzi.minestrum.content.camera;

import lombok.Getter;
import net.gotzi.minestrum.api.Callback;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Camera {

    @Getter
    private final Location defaultLocation;
    private final Callback<Entity, Boolean> isGamePlayer;
    private float yaw;

    public Camera(Location location, Callback<Entity, Boolean> isGamePlayer) {
        this.defaultLocation = location;
        this.yaw = location.getYaw();
        this.isGamePlayer = isGamePlayer;
    }

    public Optional<Player> getScan() {
        World world = defaultLocation.getWorld();
        Location newLocation = new Location(world, defaultLocation.getX(),
                defaultLocation.getY(), defaultLocation.getZ(), this.yaw, defaultLocation.getPitch());

        world.rayTraceEntities(newLocation, newLocation.getDirection(), 15, isGamePlayer::callback);

        return Optional.empty();
    }

    public float getRotationYaw() {
        return yaw;
    }

    public void rotate(int angle) {
        this.yaw += angle;
    }


}
