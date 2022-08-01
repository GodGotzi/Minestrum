package at.gotzi.minestrum.task;

public abstract class MinestrumThread extends Thread {

    public MinestrumThread(Runnable runnable, String name) {
        super(runnable, name);
    }

    public abstract void tryStop();

}
