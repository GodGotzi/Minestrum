package at.gotzi.minestrum.command.commands;

import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.api.command.CommandContext;
import at.gotzi.minestrum.api.logging.LogLevel;
import at.gotzi.minestrum.command.Command;

public class VersionCommand extends Command {

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

    private void nativeAction(CommandContext gCommandContext) {
        String version = this.minestrum.getProperties().getProperty("version");
        getCommandLogger().log(LogLevel.Info, "Version: {0}", version);

    }
}