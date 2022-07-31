package at.gotzi.minestrum.task;

public abstract class MinestrumThread extends Thread {

    public MinestrumThread(Runnable runnable) {
        super(runnable);
    }

    public abstract void tryStop();

}
