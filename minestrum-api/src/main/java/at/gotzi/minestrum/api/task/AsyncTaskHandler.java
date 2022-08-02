package at.gotzi.minestrum.api.task;

public interface AsyncTaskHandler<T extends ITask> {

    void runDelayedRepeatingTask(T task, long millis);

    void runRepeatingDelayedTask(T task, long millis);

    void runRepeatingTask(T task);

    void runDelayedTask(T task, long millis);

    void runTask(T task);

    void stopTasks();

    void stopTask(String name);

    void stopTask(T task);

}