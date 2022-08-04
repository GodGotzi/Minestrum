package at.gotzi.minestrum.command.commands;

import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.api.command.CommandContext;
import at.gotzi.minestrum.api.error.ErrorView;
import at.gotzi.minestrum.api.logging.LogLevel;
import at.gotzi.minestrum.api.registry.history.History;
import at.gotzi.minestrum.command.Command;
import at.gotzi.minestrum.command.CommandArgument;

public class ErrorCommand extends Command {

    private final Minestrum minestrum;

    public ErrorCommand(String label, Minestrum minestrum) {
        super(label);

        this.minestrum = minestrum;
        init();
    }

    @Override
    public void init() {
        CommandArgument clear = new CommandArgument("clear", 1, this::historyClear);
        CommandArgument parentHistory = new CommandArgument("history", 0, new CommandArgument[]{clear}, this::history);

        CommandArgument parentTest = new CommandArgument("test", 0, this::test);

        addArgument(parentHistory);
        addArgument(parentTest);
    }

    private void historyClear(CommandContext commandContext) {
        this.minestrum.getErrorHandler().getRegistry().getHistory().clear();
        getCommandLogger().log(LogLevel.Info, "Error History cleared!");
    }

    private void history(CommandContext commandContext) {
        History<ErrorView> history = this.minestrum.getErrorHandler().getRegistry().getHistory();
        if (history.isEmpty()) getCommandLogger().log(LogLevel.Info, "History is empty!");
        else {
            getCommandLogger().log(LogLevel.Info,
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
