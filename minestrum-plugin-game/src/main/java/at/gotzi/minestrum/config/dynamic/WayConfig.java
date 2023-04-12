package at.gotzi.minestrum.config.dynamic;

import at.gotzi.minestrum.utils.math.Vec3;

import java.util.Collection;
import java.util.Map;

public class WayConfig extends DynamicConfig<Vec3> {

    public WayConfig(String configPath) {
        super(configPath);
    }

    @Override
    public void initMap(Map<String, Vec3> valuemap) {

        Vec3 vec3;

        for (Map.Entry<Object, Object> entry : this.entrySet()) {
            if (entry.getValue() instanceof String value && entry.getKey() instanceof String key) {
                vec3 = Vec3.fromString(value);
                valuemap.put(key, vec3);
            } else {
                //TODO throw error
            }
        }
    }

    @Override
    public Vec3 getValue(String key) {
        return getValuemap().get(key);
    }

    @Override
    public void setValue(String key, Vec3 value) {
        this.getValuemap().put(key, value);
        this.setProperty(key, value.toString());
    }

    public void addNewWay(Vec3 vec) {
        int size = getValuemap().size();
        setValue("vec" + size, vec);
    }

    public Collection<Vec3> getWayCollection() {
        return getValuemap().values();
    }
}
