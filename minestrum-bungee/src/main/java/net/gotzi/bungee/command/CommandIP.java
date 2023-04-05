package net.gotzi.bungee.command;

import net.gotzi.bungee.api.CommandSender;
import net.gotzi.bungee.api.ProxyServer;
import net.gotzi.bungee.api.connection.ProxiedPlayer;

public class CommandIP extends PlayerCommand
{

    public CommandIP()
    {
        super( "ip", "bungeecord.command.ip" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if ( args.length < 1 )
        {
            sender.sendMessage( ProxyServer.getInstance().getTranslation( "username_needed" ) );
            return;
        }
        ProxiedPlayer user = ProxyServer.getInstance().getPlayer( args[0] );
        if ( user == null )
        {
            sender.sendMessage( ProxyServer.getInstance().getTranslation( "user_not_online" ) );
        } else
        {
            sender.sendMessage( ProxyServer.getInstance().getTranslation( "command_ip", args[0], user.getSocketAddress() ) );
        }
    }
}
