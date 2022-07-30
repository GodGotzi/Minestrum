package net.md_5.bungee.command;

import net.md_5.bungee.MinestrumBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Command;

public class CommandReload extends Command
{

    public CommandReload()
    {
        super( "greload", "bungeecord.command.reload" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        MinestrumBungee.getInstance().config.load();
        MinestrumBungee.getInstance().reloadMessages();
        MinestrumBungee.getInstance().stopListeners();
        MinestrumBungee.getInstance().startListeners();
        MinestrumBungee.getInstance().getPluginManager().callEvent( new ProxyReloadEvent( sender ) );

        sender.sendMessage( ChatColor.BOLD.toString() + ChatColor.RED.toString() + "BungeeCord has been reloaded."
                + " This is NOT advisable and you will not be supported with any issues that arise! Please restart BungeeCord ASAP." );
    }
}
