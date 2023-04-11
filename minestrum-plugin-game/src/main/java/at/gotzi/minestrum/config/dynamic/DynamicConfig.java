package at.gotzi.minestrum.config.dynamic;

import at.gotzi.minestrum.config.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class DynamicConfig<T> extends Config {


    private final Map<String, T> valuemap;

    public DynamicConfig(String configPath) {
        super(configPath);

        this.valuemap = new HashMap<>();
    }

    protected Map<String, T> getValuemap() {
        return valuemap;
    }

    public abstract void initMap(Map<String, T> valuemap);

    public abstract T getValue(String key);

    public abstract void setValue(String key, T value);

    public void save(String comment) throws IOException {
        FileWriter writer = new FileWriter(getConfig());
        store(writer, comment);
    }

}
