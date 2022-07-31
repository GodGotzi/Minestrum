package at.gotzi.minestrum.task;

public class Task {
    private final String name;
    private final Runnable runnable;

    private MinestrumThread minestrumThread;
    private boolean stop;

    public Task(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
        this.stop = false;
    }

    public String getName() {
        return name;
    }

    protected void run() {
        this.runnable.run();
    }

    public boolean isStopped() {
        return stop;
    }

    protected synchronized void stop() {
        minestrumThread.tryStop();
        stop = true;
    }

    public void setMinestrumThread(MinestrumThread minestrumThread) {
        this.minestrumThread = minestrumThread;
    }
}
