package net.gotzi.minestrum.api;

public interface Callback<T, R> {

    R callback(T value);

}
