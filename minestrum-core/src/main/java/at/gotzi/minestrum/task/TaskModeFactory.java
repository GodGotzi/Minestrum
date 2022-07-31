package at.gotzi.minestrum.task;

public class TaskModeFactory {

    public Runnable createDelayedTask(Task task, long millis) {
        return () -> {
            try {
                Thread.sleep(millis);
                task.run();
            } catch (InterruptedException ignored) { return; }
        };
    }

    public Runnable createRepeatingDelayedTask(Task task, long millis) {
        return () -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) { return; }
            task.run();
        };
    }

    public Runnable createDelayedRepeatingTask(Task task, long millis) {
        return () -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) { return; }
            task.run();
        };
    }








}
