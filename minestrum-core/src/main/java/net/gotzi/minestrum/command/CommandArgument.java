package net.gotzi.minestrum.command;

import lombok.NonNull;
import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.command.CommandAction;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class CommandArgument implements Comparable<CommandArgument> {
    private SortedSet<CommandArgument> subArguments;
    private final String label;
    private final int index;
    private CommandAction commandAction;

    @Comment.Constructor
    public CommandArgument(String label, int index, CommandArgument[] subArguments, CommandAction commandAction) {
        this.label = label;
        this.index = index;
        this.subArguments = new TreeSet<>(List.of(subArguments));
        this.commandAction = commandAction;
    }

    @Comment.Constructor
    public CommandArgument(String label, int index, CommandAction commandAction) {
        this.label = label;
        this.index = index;
        this.commandAction = commandAction;
    }

    @Comment.Setter
    public void setSubCommands(SortedSet<CommandArgument> subArguments) {
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
    public SortedSet<CommandArgument> getSubCommands() {
        return subArguments;
    }

    @Comment.Getter
    public CommandAction getCommandAction() {
        return commandAction;
    }

    @Override
    public int compareTo(@NonNull CommandArgument o) {
        return getLabel().compareTo(o.getLabel());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CommandArgument arg)
            return getLabel().equals(arg.getLabel());

        return false;
    }

    @Override
    public String toString() {
        return getLabel().equals("") ? "!val" : getLabel();
    }
}
