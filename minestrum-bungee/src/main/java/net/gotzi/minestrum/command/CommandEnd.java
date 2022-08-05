package net.gotzi.minestrum.command;

import com.google.common.base.Joiner;
import net.gotzi.minestrum.Bungee;
import net.gotzi.minestrum.api.ChatColor;
import net.gotzi.minestrum.api.CommandSender;
import net.gotzi.minestrum.api.plugin.Command;

/**
 * Command to terminate the proxy instance. May only be used by the console by
 * default.
 */
public class CommandEnd extends Command
{

    public CommandEnd()
    {
        super( "end", "bungeecord.command.end" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if ( args.length == 0 )
        {
            Bungee.getInstance().stop();
        } else
        {
            Bungee.getInstance().stop( ChatColor.translateAlternateColorCodes( '&', Joiner.on( ' ' ).join( args ) ) );
        }
    }
}
