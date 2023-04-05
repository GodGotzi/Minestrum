package net.gotzi.bungee.api.plugin;

import net.gotzi.bungee.api.CommandSender;

public interface TabExecutor
{

    Iterable<String> onTabComplete(CommandSender sender, String[] args);
}
