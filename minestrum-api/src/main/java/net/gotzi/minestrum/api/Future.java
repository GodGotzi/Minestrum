package net.gotzi.minestrum.api;

public interface Future<T> {

    void done(T t);
}
