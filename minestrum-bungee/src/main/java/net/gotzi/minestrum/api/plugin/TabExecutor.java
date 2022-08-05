package net.gotzi.minestrum.api.plugin;

import net.gotzi.minestrum.api.CommandSender;

public interface TabExecutor
{

    Iterable<String> onTabComplete(CommandSender sender, String[] args);
}
