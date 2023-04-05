package net.gotzi.bungee.command;

import net.gotzi.bungee.api.ChatColor;
import net.gotzi.bungee.api.CommandSender;
import net.gotzi.bungee.api.ProxyServer;
import net.gotzi.bungee.api.plugin.Command;

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
