package at.gotzi.minestrum.config.dynamic;

import org.bukkit.Location;

import java.util.Collection;

public class CameraConfig extends LocationConfig {


    public CameraConfig(String configPath) {
        super(configPath);
    }

    public void addNewCamera(Location newLocation) {
        int size = getValuemap().size();
        setValue("loc" + size, newLocation);
    }

    public Collection<Location> getCameraCollection() {
        return getValuemap().values();
    }
}
