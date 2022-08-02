package at.gotzi.minestrum.commands;

import at.gotzi.api.command.GCommand;
import at.gotzi.api.command.GCommandContext;
import at.gotzi.api.logging.GLevel;
import at.gotzi.minestrum.Minestrum;

public class VersionCommand extends GCommand {

    private final Minestrum minestrum;

    public VersionCommand(String label, Minestrum minestrum) {
        super(label);

        this.minestrum = minestrum;
        init();
    }

    @Override
    public void init() {
        setNativeAction(this::nativeAction);
    }

    private void nativeAction(GCommandContext gCommandContext) {
        String version = this.minestrum.getProperties().getProperty("version");
        getCommandLogger().log(GLevel.Info, "Version: {0}", version);

    }
}