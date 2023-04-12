package at.gotzi.minestrum.utils.math;

import org.bukkit.Location;
import org.bukkit.World;

public record Vec3(int x, int y, int z) {

    public Location transformToLocation(World world) {
        return new Location(world, x, z, y, 0f, 0f);
    }

    public static Vec3 locationToVec3(Location location) {
        return new Vec3(location.getBlockX(), location.getBlockZ(), location.getBlockY());
    }

    public static Vec3 fromString(String vec3) {
        String[] args = vec3.split(";");
        return new Vec3(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    @Override
    public String toString() {
        return x + ";" + "y" + ";" + "z";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3)) return false;

        Vec3 o = (Vec3) obj;

        return o.x == x && o.y == y && o.z == z;
    }
}
