package at.gotzi.minestrum.config.dynamic;

import at.gotzi.minestrum.utils.LocationUtils;
import org.bukkit.Location;

import java.util.Map;

public class LocationConfig extends DynamicConfig<Location> {

    public LocationConfig(String configPath) {
        super(configPath);
    }

    @Override
    public void initMap(Map<String, Location> valuemap) {

        Location location;

        for (Map.Entry<Object, Object> entry : this.entrySet()) {
            if (entry.getValue() instanceof String value && entry.getKey() instanceof String key) {
                location = LocationUtils.stringToLocation(value);
                valuemap.put(key, location);
            } else {
                //TODO throw error
            }
        }
    }

    @Override
    public Location getValue(String key) {
        return getValuemap().get(key);
    }

    @Override
    public void setValue(String key, Location value) {
        this.getValuemap().put(key, value);
        String loc = LocationUtils.locationToString(value);
        this.setProperty(key, loc);
    }
}
