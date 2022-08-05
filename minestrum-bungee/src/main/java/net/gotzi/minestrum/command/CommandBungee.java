package net.gotzi.minestrum.command;

import net.gotzi.minestrum.api.ChatColor;
import net.gotzi.minestrum.api.CommandSender;
import net.gotzi.minestrum.api.ProxyServer;
import net.gotzi.minestrum.api.plugin.Command;

public class CommandBungee extends Command
{

    public CommandBungee()
    {
        super( "bungee" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        sender.sendMessage( ChatColor.BLUE + "This server is running BungeeCord version " + ProxyServer.getInstance().getVersion() + " by md_5" );
    }
}
