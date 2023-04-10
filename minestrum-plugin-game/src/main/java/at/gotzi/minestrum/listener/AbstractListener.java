package at.gotzi.minestrum.listener;

import net.gotzi.minestrum.api.registry.Registry;

import java.util.Collections;
import java.util.List;

public abstract class AbstractListener<T> implements Registry<T> {

    private List<T> adapters;

    public AbstractListener(List<T> adapters) {
        this.adapters = adapters;
    }

    public List<T> getAdapters() {
        return Collections.unmodifiableList(this.adapters);
    }

    @Override
    public void register(T t) {
        this.register(t);
    }

    @Override
    public void unregister(T t) {
        this.unregister(t);
    }

}
