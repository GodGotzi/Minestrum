/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.utils;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.logging.LogLevel;

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
            if (i % 10 == 0 || i == 5 || i <= 3) minestrum.getLogger().log(LogLevel.WARNING, String.valueOf(i));
            Thread.sleep(increase * 1000L);
        }
    }

}
