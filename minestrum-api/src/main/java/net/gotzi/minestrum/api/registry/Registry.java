/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api.registry;

public interface Registry<T> {
    void register(T t);

    void unregister(T t);
}
