package at.gotzi.minestrum.config.dynamic;

import org.bukkit.Location;

import java.util.Collection;

public class RandomspawnConfig extends LocationConfig {
    public RandomspawnConfig(String configPath) {
        super(configPath);
    }

    public void addNewRandomspawn(Location newLocation) {
        int size = getValuemap().size();
        setValue("loc" + size, newLocation);
    }

    public Collection<Location> getRandomspawnCollection() {
        return getValuemap().values();
    }
}
