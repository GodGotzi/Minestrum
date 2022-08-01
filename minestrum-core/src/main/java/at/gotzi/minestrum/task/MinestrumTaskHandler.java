package at.gotzi.minestrum.task;

import at.gotzi.api.template.Register;

import java.util.LinkedHashMap;
import java.util.Map;

public class MinestrumTaskHandler implements AsyncTaskHandler, Register<Task> {

    private final Map<String, Task> taskMap = new LinkedHashMap<>();

    @Override
    public void runDelayedRepeatingTask(Task task, long millis) {

    }

    @Override
    public void runRepeatingTask(Task task) {

    }

    @Override
    public void runDelayedTask(Task task, long millis) {
        this.register(task);

        MinestrumThread minestrumThread = new MinestrumThread(task::run, task.getName()) {
            @Override
            public void tryStop() {
                unregister(task);
                this.interrupt();
            }
        };

        minestrumThread.start();
    }

    @Override
    public void runTask(Task task) {
        this.register(task);

        MinestrumThread minestrumThread = new MinestrumThread(task::run, task.getName()) {
            @Override
            public void tryStop() {
                this.stop();
            }
        };

        task.setMinestrumThread(minestrumThread);
        minestrumThread.start();
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