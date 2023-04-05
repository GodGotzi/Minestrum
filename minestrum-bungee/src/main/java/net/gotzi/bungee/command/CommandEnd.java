package net.gotzi.bungee.command;

import com.google.common.base.Joiner;
import net.gotzi.bungee.api.plugin.Command;
import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.api.ChatColor;
import net.gotzi.bungee.api.CommandSender;

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
