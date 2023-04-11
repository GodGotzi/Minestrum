package at.gotzi.minestrum;

import at.gotzi.minestrum.config.dynamic.LocationConfig;
import at.gotzi.minestrum.config.dynamic.RandomspawnConfig;
import lombok.Getter;
import org.bukkit.Location;

import java.util.*;

public class LocationCoordinator {

    @Getter
    private final LocationConfig locationConfig;

    @Getter
    private final RandomspawnConfig randomspawnConfig;


    public LocationCoordinator(LocationConfig locationConfig, RandomspawnConfig randomspawnConfig) {
        this.locationConfig = locationConfig;
        this.randomspawnConfig = randomspawnConfig;
    }

    public Location getRandomLocation() {
        Random rand = new Random();
        List<Location> locations =
                new ArrayList<>(this.randomspawnConfig.getRandomspawnCollection());

        return locations.get(rand.nextInt(locations.size()));
    }

}
