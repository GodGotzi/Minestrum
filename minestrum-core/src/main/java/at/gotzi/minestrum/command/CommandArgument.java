package at.gotzi.minestrum.command;

import at.gotzi.minestrum.ano.Comment;
import at.gotzi.minestrum.api.command.CommandAction;

public class CommandArgument {

    private CommandArgument[] subArguments;
    private final String label;
    private final int index;
    private CommandAction commandAction;

    @Comment.Constructor
    public CommandArgument(String label, int index, CommandArgument[] subArguments, CommandAction commandAction) {
        this.label = label;
        this.index = index;
        this.subArguments = subArguments;
        this.commandAction = commandAction;
    }

    @Comment.Constructor
    public CommandArgument(String label, int index, CommandAction commandAction) {
        this.label = label;
        this.index = index;
        this.commandAction = commandAction;
    }

    @Comment.Setter
    public void setSubCommands(CommandArgument[] subArguments) {
        this.subArguments = subArguments;
    }

    @Comment.Setter
    public void setCommandAction(CommandAction commandAction) {
        this.commandAction = commandAction;
    }

    @Comment.Getter
    public String getLabel() {
        return label;
    }

    @Comment.Getter
    public int getIndex() {
        return index;
    }

    @Comment.Getter
    public CommandArgument[] getSubCommands() {
        return subArguments;
    }

    @Comment.Getter
    public CommandAction getCommandAction() {
        return commandAction;
    }
}
