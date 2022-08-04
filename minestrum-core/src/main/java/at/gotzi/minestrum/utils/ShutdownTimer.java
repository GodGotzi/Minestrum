package at.gotzi.minestrum.utils;

import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.ano.Comment;
import at.gotzi.minestrum.api.logging.LogLevel;

public class ShutdownTimer {

    public static void startDefaultShutdown(Minestrum minestrum) {
        ShutdownTimer shutdownTimer = new ShutdownTimer(minestrum, 30000, 1000);
        try {
            shutdownTimer.start();
        } catch (InterruptedException e) {
            System.exit(0);
        }

        System.exit(0);
    }

    private final Minestrum minestrum;
    private final int time;
    private final int increase;

    @Comment.Constructor
    public ShutdownTimer(Minestrum minestrum, int time, int increase) {
        this.time = time/1000;
        this.increase = increase/1000;
        this.minestrum = minestrum;
    }

    @Comment.Init
    public void start() throws InterruptedException {
        for (int i = time/increase; i > 0; i--) {
            if (i % 10 == 0 || i == 5 || i <= 3) minestrum.getLogger().log(LogLevel.Warning, String.valueOf(i));
            Thread.sleep(increase * 1000L);
        }
    }

}
