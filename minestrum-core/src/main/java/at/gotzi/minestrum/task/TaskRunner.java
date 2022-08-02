package at.gotzi.minestrum.task;

public class TaskRunner {

    public Runnable delayedTask(Runnable task, long millis) {
        return () -> {
            try {
                Thread.sleep(millis);
                if (!Thread.currentThread().isInterrupted())
                    task.run();
            } catch (InterruptedException ignored) { }
        };
    }

    public Runnable repeatingDelayedTask(Runnable task, long millis) {
        return () -> {
            while (true && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ignored) { }
            }
        };
    }

    public Runnable repeatingTask(Runnable task) {
        return () -> {
            while(true && !Thread.currentThread().isInterrupted()) task.run();
        };
    }

    public Runnable delayedRepeatingTask(Runnable task, long millis) {
        return () -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) { }
            while(true && !Thread.currentThread().isInterrupted()) task.run();
        };
    }
}