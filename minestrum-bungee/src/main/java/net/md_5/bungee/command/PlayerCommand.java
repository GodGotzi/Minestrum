package net.md_5.bungee.command;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Locale;
import java.util.stream.Collectors;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * @deprecated internal use only
 */
@Deprecated
public abstract class PlayerCommand extends Command implements TabExecutor {

    public PlayerCommand(String name)
    {
        super( name );
    }

    public PlayerCommand(String name, String permission, String... aliases)
    {
        super( name, permission, aliases );
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        final String lastArg = ( args.length > 0 ) ? args[args.length - 1].toLowerCase( Locale.ROOT ) : "";
        return Iterables.transform(
                ProxyServer.getInstance()
                        .getPlayers()
                        .stream()
                        .filter(player -> player.getName()
                                .toLowerCase(Locale.ROOT)
                                .startsWith(lastArg))
                        .collect(Collectors.toList()), CommandSender::getName
        );
    }
}
