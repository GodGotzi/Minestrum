package at.gotzi.minestrum.task;

import at.gotzi.minestrum.api.registry.Registry;
import at.gotzi.minestrum.api.task.AsyncTaskHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class TaskHandler implements AsyncTaskHandler<Task>, Registry<Task> {

    private final Map<String, Task> taskMap = new LinkedHashMap<>();

    private final TaskRunner taskRunner;

    public TaskHandler() {
        this.taskRunner = new TaskRunner();
    }

    @Override
    public void runDelayedRepeatingTask(Task task, long millis) {
        this.register(task);

        Runnable threadTask = this.taskRunner.delayedRepeatingTask(task::run, millis);
        TaskThread taskThread = new TaskThread(threadTask, taskMap::remove, task.getName());
        task.setThread(taskThread);
        taskThread.start();
    }

    @Override
    public void runRepeatingTask(Task task) {
        this.register(task);

        Runnable threadTask = this.taskRunner.repeatingTask(task::run);
        TaskThread taskThread = new TaskThread(threadTask, taskMap::remove, task.getName());
        task.setThread(taskThread);
        taskThread.start();
    }

    @Override
    public void runRepeatingDelayedTask(Task task, long millis) {
        this.register(task);

        Runnable threadTask = this.taskRunner.repeatingDelayedTask(task::run, millis);
        TaskThread taskThread = new TaskThread(threadTask, taskMap::remove, task.getName());
        task.setThread(taskThread);
        taskThread.start();
    }

    @Override
    public void runDelayedTask(Task task, long millis) {
        this.register(task);

        Runnable threadTask = this.taskRunner.delayedTask(task::run, millis);
        TaskThread taskThread = new TaskThread(threadTask, taskMap::remove, task.getName());
        task.setThread(taskThread);
        taskThread.start();
    }

    @Override
    public void runTask(Task task) {
        this.register(task);

        TaskThread taskThread = new TaskThread(task::run, taskMap::remove, task.getName());
        task.setThread(taskThread);
        taskThread.start();
    }

    @Override
    public void register(Task task) {
        taskMap.put(task.getName(), task);
    }

    @Override
    public void unregister(Task task) {
        taskMap.remove(task.getName());
    }

    public void stopTask(String name) {
        taskMap.get(name).stop();
    }

    @Override
    public void stopTask(Task task) {
        task.stop();
    }

    public void stopTasks() {
        taskMap.values().forEach(Task::stop);
        taskMap.clear();
    }
}