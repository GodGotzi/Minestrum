package net.gotzi.minestrum.api.registry;

public interface Registry<T> {
    void register(T t);

    void unregister(T t);
}
