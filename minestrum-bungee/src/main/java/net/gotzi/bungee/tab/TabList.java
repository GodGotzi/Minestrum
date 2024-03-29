package net.gotzi.bungee.tab;

import lombok.RequiredArgsConstructor;
import net.gotzi.bungee.UserConnection;
import net.gotzi.bungee.protocol.Property;
import net.gotzi.bungee.connection.LoginResult;
import net.gotzi.bungee.protocol.packet.PlayerListItem;
import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.api.connection.ProxiedPlayer;

@RequiredArgsConstructor
public abstract class TabList
{

    protected final ProxiedPlayer player;

    public abstract void onUpdate(PlayerListItem playerListItem);

    public abstract void onPingChange(int ping);

    public abstract void onServerChange();

    public abstract void onConnect();

    public abstract void onDisconnect();

    public static PlayerListItem rewrite(PlayerListItem playerListItem)
    {
        for ( PlayerListItem.Item item : playerListItem.getItems() )
        {
            if ( item.getUuid() == null ) // Old style ping
            {
                continue;
            }
            UserConnection player = Bungee.getInstance().getPlayerByOfflineUUID( item.getUuid() );
            if ( player != null )
            {
                item.setUuid( player.getUniqueId() );
                LoginResult loginResult = player.getPendingConnection().getLoginProfile();
                if ( loginResult != null && loginResult.getProperties() != null )
                {
                    Property[] props = new Property[ loginResult.getProperties().length ];
                    for ( int i = 0; i < props.length; i++ )
                    {
                        props[i] = new Property( loginResult.getProperties()[i].getName(), loginResult.getProperties()[i].getValue(), loginResult.getProperties()[i].getSignature() );
                    }
                    item.setProperties( props );
                } else
                {
                    item.setProperties( new Property[ 0 ] );
                }
                if ( playerListItem.getAction() == PlayerListItem.Action.ADD_PLAYER || playerListItem.getAction() == PlayerListItem.Action.UPDATE_GAMEMODE )
                {
                    player.setGamemode( item.getGamemode() );
                }
                if ( playerListItem.getAction() == PlayerListItem.Action.ADD_PLAYER || playerListItem.getAction() == PlayerListItem.Action.UPDATE_LATENCY )
                {
                    player.setPing( item.getPing() );
                }
            }
        }
        return playerListItem;
    }
}
