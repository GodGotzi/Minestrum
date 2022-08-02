package at.gotzi.minestrum.task;

public class TaskRunner {

    public Runnable delayedTask(Runnable task, long millis) {
        return () -> {
            try {
                Thread.sleep(millis);
                task.run();
            } catch (InterruptedException ignored) { return; }
            task.run();
        };
    }

    public Runnable repeatingDelayedTask(Runnable task, long millis) {
        return () -> {
            while (true) {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ignored) {
                    return;
                }
                task.run();
            }
        };
    }

    public Runnable repeatingTask(Runnable task) {
        return () -> {
            while(true) task.run();
        };
    }

    public Runnable delayedRepeatingTask(Runnable task, long millis) {
        return () -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) { return; }
            while(true) task.run();
        };
    }
}
