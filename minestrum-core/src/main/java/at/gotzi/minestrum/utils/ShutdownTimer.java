package at.gotzi.minestrum.utils;

import at.gotzi.api.ano.Comment;
import at.gotzi.minestrum.Minestrum;

public class ShutdownTimer {

    public static void startDefaultShutdown() {
        startShutdown(30000, 1000);
    }

    public static void startShutdown(int time, int increase) {
        ShutdownTimer shutdownTimer = new ShutdownTimer(Minestrum.getInstance(), time, increase);

        try {
            shutdownTimer.start();
        } catch (InterruptedException e) {
            Minestrum.getInstance().getLogger().warning("Shutdown Time got interrupted... forcing Shutdown!");
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
            if (i % 10 == 0 || i == 5 || i <= 3) minestrum.getLogger().warning(String.valueOf(i));
            Thread.sleep(increase * 1000L);
        }
    }

}
