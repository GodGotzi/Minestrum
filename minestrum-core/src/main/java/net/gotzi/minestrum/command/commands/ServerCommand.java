package net.gotzi.minestrum.command.commands;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.command.Command;
import net.gotzi.minestrum.command.CommandArgument;
import net.gotzi.spigot.FakeServer;
import net.gotzi.spigot.data.GameData;
import net.gotzi.spigot.data.ServerData;

import java.util.UUID;

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

        GameData gameData = new GameData(1);
        ServerData serverData = new ServerData(UUID.randomUUID(), gameData);
        FakeServer fakeServer = new FakeServer(serverData);
        //fakeServer.run();
        getCommandLogger().log(LogLevel.DEBUG, "command test, if you see this the test should have been started");
    }
}
