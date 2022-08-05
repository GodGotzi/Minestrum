package net.gotzi.minestrum.command;

import java.util.HashSet;
import java.util.Set;
import net.gotzi.minestrum.Util;
import net.gotzi.minestrum.api.CommandSender;
import net.gotzi.minestrum.api.ProxyServer;
import net.gotzi.minestrum.api.plugin.Command;

public class CommandPerms extends Command
{

    public CommandPerms()
    {
        super( "perms" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        Set<String> permissions = new HashSet<>();
        for ( String group : sender.getGroups() )
        {
            permissions.addAll( ProxyServer.getInstance().getConfigurationAdapter().getPermissions( group ) );
        }
        sender.sendMessage( ProxyServer.getInstance().getTranslation( "command_perms_groups", Util.csv( sender.getGroups() ) ) );

        for ( String permission : permissions )
        {
            sender.sendMessage( ProxyServer.getInstance().getTranslation( "command_perms_permission", permission ) );
        }
    }
}
