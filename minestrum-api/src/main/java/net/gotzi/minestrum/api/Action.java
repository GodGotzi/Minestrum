/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api;

public interface Action<T> {

    void run(T t);

}
