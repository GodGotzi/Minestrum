/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api.task;

public interface ITask<T extends Thread> {

    String getName();

    void run();

    boolean isStopped();

    void stop();

    void setThread(T thread);
}
