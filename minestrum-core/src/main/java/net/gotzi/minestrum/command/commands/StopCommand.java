package net.gotzi.minestrum.command.commands;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.command.Command;

public class StopCommand extends Command {

    private final Minestrum minestrum;

    public StopCommand(String label, Minestrum minestrum) {
        super(label);

        this.minestrum = minestrum;
        init();
    }

    @Override
    public void init() {
        setNativeAction(this::nativeAction);
    }

    private void nativeAction(CommandContext commandContext) {
        minestrum.stop();
    }
}