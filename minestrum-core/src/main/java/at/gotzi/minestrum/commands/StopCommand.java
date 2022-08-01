package at.gotzi.minestrum.commands;

import at.gotzi.api.command.GCommand;
import at.gotzi.api.command.GCommandContext;
import at.gotzi.minestrum.Minestrum;

public class StopCommand extends GCommand {

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

    private void nativeAction(GCommandContext commandContext) {
        minestrum.stop();
    }
}