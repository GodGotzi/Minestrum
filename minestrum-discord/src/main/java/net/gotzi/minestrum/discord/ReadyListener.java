package net.gotzi.minestrum.discord;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReadyListener extends ListenerAdapter {
    private final AtomicBoolean finished;

    public ReadyListener(AtomicBoolean finished) {
        this.finished = finished;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        finished.set(true);
    }
}