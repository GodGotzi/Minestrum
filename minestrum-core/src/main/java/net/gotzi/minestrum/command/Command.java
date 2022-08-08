package net.gotzi.minestrum.command;

import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.Executable;
import net.gotzi.minestrum.api.Initializable;
import net.gotzi.minestrum.api.command.CommandAction;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.logging.MinestrumLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public abstract class Command implements Executable<CommandContext>, Initializable {

    private final List<CommandArgument> arguments = new ArrayList<>();
    private final String label;
    private CommandAction nativeAction;
    private Logger logger;

    @Comment.Constructor
    public Command(String label) {
        this.label = label;
    }

    @Comment.Constructor
    public Command(String label, CommandAction nativeAction) {
        this.label = label;
        this.nativeAction = nativeAction;
    }

    /**
     * If the command has no arguments, run the nullAction. If the command has arguments, check if the first argument is
     * valid. If it is, check if the next argument is valid. If it is, check if the next argument is valid. If it is, run
     * the command action
     *
     * @param gotziCommandContext The context of the command.
     */
    @Override
    public void execute(CommandContext gotziCommandContext) {
        if (gotziCommandContext.args().length == 0 && nativeAction != null) {
            nativeAction.run(gotziCommandContext);
            return;
        }

        CommandArgument gArgument =
                arguments.stream().filter(arg -> filterGotziArguments(arg, gotziCommandContext))
                        .findFirst().orElse(null);

        if (gArgument == null) {
            getCommandLogger().log(LogLevel.Info, gotziCommandContext.properties().getProperty("onFalseSyntax"),
                    gotziCommandContext.cmd());
            return;
        } else if (gotziCommandContext.args().length == 1)
            gArgument.getCommandAction().run(gotziCommandContext);

        for (int i = 0; i < gotziCommandContext.args().length-1; i++) {
            gArgument = getNextArgument(gArgument, gotziCommandContext);
            if (gArgument == null) {
                getCommandLogger().log(LogLevel.Info, gotziCommandContext.properties().getProperty("onFalseSyntax"),
                        gotziCommandContext.cmd());
                return;
            }

            if (gArgument.getIndex() == (gotziCommandContext.args().length-1) && gArgument.getCommandAction() != null) {
                gArgument.getCommandAction().run(gotziCommandContext);
            }
        }
    }

    public int tabComplete(String buffer, int cursor, List<CharSequence> candidates) {


        return 0;
    }

    /**
     * It returns the next argument in the command chain
     *
     * @param gArgument The current argument that is being processed.
     * @param gotziCommandContext The context of the command.
     * @return The next argument in the command.
     */
    private CommandArgument getNextArgument(CommandArgument gArgument, CommandContext gotziCommandContext) {
        if (gArgument.getSubCommands() == null) return null;
        return Arrays.stream(gArgument.getSubCommands()).filter(arg -> filterGotziArguments(gArgument, gotziCommandContext)).findFirst().orElse(null);
    }

    /**
     * "If the argument's index is less than the number of arguments in the command context, and the argument's label is
     * equal to the argument at the index in the command context, or the argument is a value argument, return true."
     *
     * The first part of the function checks if the argument's index is less than the number of arguments in the command
     * context. This is to make sure that the argument is not out of bounds
     *
     * @param gArgument The argument that is being checked.
     * @param gotziCommandContext The context of the command.
     * @return A boolean value.
     */
    private boolean filterGotziArguments(CommandArgument gArgument, CommandContext gotziCommandContext) {
        return gArgument.getIndex() < gotziCommandContext.args().length &&
                (gArgument.getLabel().equals(gotziCommandContext.args()[gArgument.getIndex()])
                        || gArgument instanceof CommandArgumentValue);
    }

    /**
     * If the argument doesn't already exist, add it to the list
     *
     * @param gArgument The argument to add to the list of arguments.
     */
    public void addArgument(CommandArgument gArgument) {
        if (arguments.stream().noneMatch(argument -> argument.getLabel().equals(gArgument.getLabel())))
            arguments.add(gArgument);
    }

    @Comment.Getter
    public String getLabel() {
        return label;
    }

    @Comment.Getter
    public List<CommandArgument> getArguments() {
        return arguments;
    }

    public void setNativeAction(CommandAction nativeAction) {
        this.nativeAction = nativeAction;
    }

    public Logger getCommandLogger() {
        return this.logger;
    }

    protected void setLogger(Logger logger) {
        this.logger = logger;
    }
}