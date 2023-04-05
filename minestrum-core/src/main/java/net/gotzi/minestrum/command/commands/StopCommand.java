/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.command.commands;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.command.Command;

public class StopCommand extends Command {

    private final Minestrum minestrum;

    public StopCommand(String label, Minestrum minestrum) {
        super(label);

        this.minestrum = minestrum;
        init();
    }

    public void init() {
        setNativeAction(this::nativeAction);
    }

    private void nativeAction(CommandContext commandContext) {
        minestrum.stop();
    }
}