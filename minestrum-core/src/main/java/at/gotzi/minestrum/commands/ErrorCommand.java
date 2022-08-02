package at.gotzi.minestrum.commands;

import at.gotzi.api.command.GArgument;
import at.gotzi.api.command.GCommand;
import at.gotzi.api.command.GCommandContext;
import at.gotzi.api.logging.GLevel;
import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.api.error.ErrorView;
import at.gotzi.minestrum.api.registry.History;

public class ErrorCommand extends GCommand {

    private final Minestrum minestrum;

    public ErrorCommand(String label, Minestrum minestrum) {
        super(label);

        this.minestrum = minestrum;
        init();
    }

    @Override
    public void init() {
        GArgument clear = new GArgument("clear", 1, this::historyClear);
        GArgument parentHistory = new GArgument("history", 0, new GArgument[]{clear}, this::history);

        GArgument parentTest = new GArgument("test", 0, this::test);

        addArgument(parentHistory);
        addArgument(parentTest);
    }

    private void historyClear(GCommandContext commandContext) {
        this.minestrum.getErrorHandler().getRegistry().getHistory().clear();
        getCommandLogger().log(GLevel.Info, "Error History cleared!");
    }

    private void history(GCommandContext commandContext) {
        History<ErrorView> history = this.minestrum.getErrorHandler().getRegistry().getHistory();
        if (history.isEmpty()) getCommandLogger().log(GLevel.Info, "History is empty!");
        else {
            getCommandLogger().log(GLevel.Info,
                    "History\n" + "-".repeat(25) + "\n" + history + "-".repeat(25)
            );
        }
    }

    private void test(GCommandContext commandContext) {
        try {
            throw new Exception();
        } catch (Exception e) {
            ErrorView errorView = new ErrorView("Test", e);
            this.minestrum.getErrorHandler().registerError(errorView);
        }
    }


}
