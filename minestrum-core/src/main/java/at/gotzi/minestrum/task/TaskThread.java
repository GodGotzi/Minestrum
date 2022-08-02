package at.gotzi.minestrum.task;

import at.gotzi.minestrum.api.registry.UnregisterAction;

public class TaskThread extends Thread {

    private final UnregisterAction<String> unregisterAction;

    protected TaskThread(Runnable runnable, UnregisterAction<String> unregisterAction, String name) {
        super(runnable, name);
        this.unregisterAction = unregisterAction;
    }

    public void tryStop() {
        unregisterAction.run(getName());
        interrupt();
    }
}
