/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.command.commands;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.registry.history.History;
import net.gotzi.minestrum.command.Command;
import net.gotzi.minestrum.command.CommandArgument;

public class ErrorCommand extends Command {

    private final Minestrum minestrum;

    public ErrorCommand(String label, Minestrum minestrum) {
        super(label);

        this.minestrum = minestrum;
        init();
    }

    public void init() {
        CommandArgument clear = new CommandArgument("clear", 1, this::historyClear);
        CommandArgument parentHistory = new CommandArgument("history", 0, new CommandArgument[]{clear}, this::history);

        CommandArgument parentTest = new CommandArgument("test", 0, this::test);

        addArgument(parentHistory);
        addArgument(parentTest);
    }

    private void historyClear(CommandContext commandContext) {
        this.minestrum.getErrorHandler().getRegistry().getHistory().clear();
        getCommandLogger().log(LogLevel.INFO, "Error History cleared!");
    }

    private void history(CommandContext commandContext) {
        History<ErrorView> history = this.minestrum.getErrorHandler().getRegistry().getHistory();
        if (history.isEmpty()) getCommandLogger().log(LogLevel.INFO, "History is empty!");
        else {
            getCommandLogger().log(LogLevel.INFO,
                    "History\n" + "-".repeat(100) + "\n" + history + "-".repeat(100)
            );
        }
    }
    private void test(CommandContext commandContext) {
        try {
            throw new Exception();
        } catch (Exception e) {
            ErrorView errorView = new ErrorView("Test", e);
            this.minestrum.getErrorHandler().registerError(errorView);
        }
    }


}
