package at.gotzi.minestrum.task;

import at.gotzi.minestrum.api.registry.Registry;
import at.gotzi.minestrum.api.task.AsyncTaskScheduler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaskScheduler implements AsyncTaskScheduler<Task>, Registry<Task> {

    private final Map<String, Task> taskMap = new LinkedHashMap<>();
    private final TaskRunFactory taskRunFactory;

    public TaskScheduler() {
        this.taskRunFactory = new TaskRunFactory();
    }

    @Override
    public void runDelayedRepeatingTask(Task task, long millis) {
        this.register(task);

        Runnable threadTask = this.taskRunFactory.delayedRepeatingTask(task::run, millis);
        TaskThread taskThread = new TaskThread(threadTask, taskMap::remove, task.getName());
        task.setThread(taskThread);
        taskThread.start();
    }

    @Override
    public void runRepeatingTask(Task task) {
        this.register(task);

        Runnable threadTask = this.taskRunFactory.repeatingTask(task::run);
        TaskThread taskThread = new TaskThread(threadTask, taskMap::remove, task.getName());
        task.setThread(taskThread);
        taskThread.start();
    }

    @Override
    public void runRepeatingDelayedTask(Task task, long millis) {
        this.register(task);

        Runnable threadTask = this.taskRunFactory.repeatingDelayedTask(task::run, millis);
        TaskThread taskThread = new TaskThread(threadTask, taskMap::remove, task.getName());
        task.setThread(taskThread);
        taskThread.start();
    }

    @Override
    public void runDelayedTask(Task task, long millis) {
        this.register(task);

        Runnable threadTask = this.taskRunFactory.delayedTask(task::run, millis);
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
        new ArrayList<>(taskMap.values()).forEach(Task::stop);
    }
}