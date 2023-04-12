package at.gotzi.minestrum.ai;

import at.gotzi.minestrum.utils.math.Vec3;
import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;

public class PathController {

    private static int splitAmount = 10;

    private Vec3 corner1;
    private Vec3 corner2;

    private List<Vec3> wayBlocks;

    public PathController(Vec3 corner1, Vec3 corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.wayBlocks = listAllWayVectors();
    }

    private List<Vec3> listAllWayVectors() {
        List<Vec3> list = new LinkedList<>();

        for (int x = Math.min(corner1.x(), corner2.x()); x <= Math.max(corner1.x(), corner2.x()); x++) {
            for (int y = Math.min(corner1.y(), corner2.y()); y <= Math.max(corner1.y(), corner2.y()); y++) {
                for (int z = Math.min(corner1.z(), corner2.z()); z <= Math.max(corner1.z(), corner2.z()); z++) {
                    Vec3 point = new Vec3(x, y, z);
                    list.add(point);
                }
            }
        }

        return list;
    }

    public Vec3 getVectorToGoTo(Vec3 playerLocation, Vec3 monstrumLocation) {




        return null;
    }

}
