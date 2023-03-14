/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.command.commands;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.command.Command;
import net.gotzi.minestrum.command.CommandArgument;
public class ServerCommand extends Command {

    private final Minestrum minestrum;

    public ServerCommand(String label, Minestrum minestrum) {
        super(label);

        this.minestrum = minestrum;

        init();
    }

    private void init() {
        CommandArgument subStartTest = new CommandArgument("test", 1, this::test);
        CommandArgument start = new CommandArgument("start", 0, new CommandArgument[]{subStartTest}, null);

        addArgument(start);
    }

    private void test(CommandContext commandContext) {

        getCommandLogger().log(LogLevel.DEBUG, "command test, if you see this the test should have been started");
    }
}
