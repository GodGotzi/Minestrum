package at.gotzi.minestrum.task;

public interface AsyncTaskHandler {

    void runDelayedRepeatingTask(Task task, long millis);

    void runRepeatingTask(Task task);

    void runDelayedTask(Task task, long millis);

    void runTask(Task task);

    void stopTasks();

    void stopTask(String name);

    void stopTask(Task task);

}
