/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.task;

import net.gotzi.minestrum.api.task.ITask;

public class Task implements ITask<TaskThread> {
    private final String name;
    private final Runnable runnable;

    private TaskThread taskThread;
    private boolean stop;

    public Task(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
        this.stop = false;
    }

    public TaskThread getTaskThread() {
        return taskThread;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void run() {
        this.runnable.run();
    }

    @Override
    public boolean isStopped() {
        return stop;
    }

    @Override
    public synchronized void stop() {
        taskThread.tryStop();
        stop = true;
    }

    @Override
    public void setThread(TaskThread thread) {
        this.taskThread = thread;
    }
}