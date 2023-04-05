package net.gotzi.bungee.command;

import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.api.ChatColor;
import net.gotzi.bungee.api.CommandSender;
import net.gotzi.bungee.api.event.ProxyReloadEvent;
import net.gotzi.bungee.api.plugin.Command;

public class CommandReload extends Command
{

    public CommandReload()
    {
        super( "greload", "bungeecord.command.reload" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        Bungee.getInstance().config.load();
        Bungee.getInstance().reloadMessages();
        Bungee.getInstance().stopListeners();
        Bungee.getInstance().startListeners();
        Bungee.getInstance().getPluginManager().callEvent( new ProxyReloadEvent( sender ) );

        sender.sendMessage( ChatColor.BOLD.toString() + ChatColor.RED.toString() + "BungeeCord has been reloaded."
                + " This is NOT advisable and you will not be supported with any issues that arise! Please restart BungeeCord ASAP." );
    }
}
