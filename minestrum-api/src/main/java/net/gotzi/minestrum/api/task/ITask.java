package net.gotzi.minestrum.api.task;

public interface ITask<T extends Thread> {

    String getName();

    void run();

    boolean isStopped();

    void stop();

    void setThread(T thread);
}
