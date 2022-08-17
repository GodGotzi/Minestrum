package net.gotzi.minestrum.command.commands;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.registry.history.History;
import net.gotzi.minestrum.command.Command;
import net.gotzi.minestrum.command.CommandArgument;

import java.util.logging.LogRecord;

public class LogCommand extends Command {

    private final Minestrum minestrum;

    public LogCommand(String label, Minestrum minestrum) {
        super(label);

        this.minestrum = minestrum;
        init();
    }

    @Override
    public void init() {
        CommandArgument clear = new CommandArgument("clear", 1, this::historyClear);
        CommandArgument parentHistory = new CommandArgument("history", 0, new CommandArgument[]{clear}, this::history);

        addArgument(parentHistory);
    }

    private void historyClear(CommandContext commandContext) {
        this.minestrum.getLogHandler().getRegistry().getHistory().clear();
        getCommandLogger().log(LogLevel.Info, "Log History cleared!");
    }

    private void history(CommandContext commandContext) {
        History<LogRecord> history = this.minestrum.getLogHandler().getRegistry().getHistory();
        if (history.isEmpty()) getCommandLogger().log(LogLevel.Info, "History is empty!");
        else {
            getCommandLogger().log(LogLevel.Info,
                    "History\n" + "-".repeat(100) + "\n" + history + "-".repeat(100)
            );
        }
    }
}


