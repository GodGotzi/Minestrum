package net.gotzi.minestrum.command.commands;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.command.Command;

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