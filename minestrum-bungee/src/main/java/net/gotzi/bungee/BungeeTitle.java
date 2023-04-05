package net.gotzi.bungee;

import lombok.Data;
import net.gotzi.bungee.protocol.packet.Title;
import net.gotzi.bungee.api.chat.BaseComponent;
import net.gotzi.bungee.api.connection.ProxiedPlayer;
import net.gotzi.bungee.chat.ComponentSerializer;
import net.gotzi.bungee.protocol.DefinedPacket;
import net.gotzi.bungee.protocol.ProtocolConstants;
import net.gotzi.bungee.protocol.packet.ClearTitles;
import net.gotzi.bungee.protocol.packet.Subtitle;
import net.gotzi.bungee.protocol.packet.TitleTimes;

public class BungeeTitle implements net.gotzi.bungee.api.Title
{

    private TitlePacketHolder<Title> title;
    private TitlePacketHolder<Subtitle> subtitle;
    private TitlePacketHolder<TitleTimes> times;
    private TitlePacketHolder<ClearTitles> clear;
    private TitlePacketHolder<ClearTitles> reset;

    @Data
    private static class TitlePacketHolder<T extends DefinedPacket>
    {

        private final Title oldPacket;
        private final T newPacket;
    }

    private static TitlePacketHolder<TitleTimes> createAnimationPacket()
    {
        TitlePacketHolder<TitleTimes> title = new TitlePacketHolder<>( new Title( Title.Action.TIMES ), new TitleTimes() );

        title.oldPacket.setFadeIn( 20 );
        title.oldPacket.setStay( 60 );
        title.oldPacket.setFadeOut( 20 );

        title.newPacket.setFadeIn( 20 );
        title.newPacket.setStay( 60 );
        title.newPacket.setFadeOut( 20 );

        return title;
    }

    @Override
    public net.gotzi.bungee.api.Title title(BaseComponent text)
    {
        if ( title == null )
        {
            Title packet = new Title( Title.Action.TITLE );
            title = new TitlePacketHolder<>( packet, packet );
        }

        title.oldPacket.setText( ComponentSerializer.toString( text ) ); // = newPacket
        return this;
    }

    @Override
    public net.gotzi.bungee.api.Title title(BaseComponent... text)
    {
        if ( title == null )
        {
            Title packet = new Title( Title.Action.TITLE );
            title = new TitlePacketHolder<>( packet, packet );
        }

        title.oldPacket.setText( ComponentSerializer.toString( text ) ); // = newPacket
        return this;
    }

    @Override
    public net.gotzi.bungee.api.Title subTitle(BaseComponent text)
    {
        if ( subtitle == null )
        {
            subtitle = new TitlePacketHolder<>( new Title( Title.Action.SUBTITLE ), new Subtitle() );
        }

        String serialized = ComponentSerializer.toString( text );
        subtitle.oldPacket.setText( serialized );
        subtitle.newPacket.setText( serialized );
        return this;
    }

    @Override
    public net.gotzi.bungee.api.Title subTitle(BaseComponent... text)
    {
        if ( subtitle == null )
        {
            subtitle = new TitlePacketHolder<>( new Title( Title.Action.SUBTITLE ), new Subtitle() );
        }

        String serialized = ComponentSerializer.toString( text );
        subtitle.oldPacket.setText( serialized );
        subtitle.newPacket.setText( serialized );
        return this;
    }

    @Override
    public net.gotzi.bungee.api.Title fadeIn(int ticks)
    {
        if ( times == null )
        {
            times = createAnimationPacket();
        }

        times.oldPacket.setFadeIn( ticks );
        times.newPacket.setFadeIn( ticks );
        return this;
    }

    @Override
    public net.gotzi.bungee.api.Title stay(int ticks)
    {
        if ( times == null )
        {
            times = createAnimationPacket();
        }

        times.oldPacket.setStay( ticks );
        times.newPacket.setStay( ticks );
        return this;
    }

    @Override
    public net.gotzi.bungee.api.Title fadeOut(int ticks)
    {
        if ( times == null )
        {
            times = createAnimationPacket();
        }

        times.oldPacket.setFadeOut( ticks );
        times.newPacket.setFadeOut( ticks );
        return this;
    }

    @Override
    public net.gotzi.bungee.api.Title clear()
    {
        if ( clear == null )
        {
            clear = new TitlePacketHolder<>( new Title( Title.Action.CLEAR ), new ClearTitles() );
        }

        title = null; // No need to send title if we clear it after that again

        return this;
    }

    @Override
    public net.gotzi.bungee.api.Title reset()
    {
        if ( reset == null )
        {
            reset = new TitlePacketHolder<>( new Title( Title.Action.RESET ), new ClearTitles( true ) );
        }

        // No need to send these packets if we reset them later
        title = null;
        subtitle = null;
        times = null;

        return this;
    }

    private static void sendPacket(ProxiedPlayer player, TitlePacketHolder packet)
    {
        if ( packet != null )
        {
            if ( player.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_17 )
            {
                player.unsafe().sendPacket( packet.newPacket );
            } else
            {
                player.unsafe().sendPacket( packet.oldPacket );
            }
        }
    }

    @Override
    public net.gotzi.bungee.api.Title send(ProxiedPlayer player)
    {
        sendPacket( player, clear );
        sendPacket( player, reset );
        sendPacket( player, times );
        sendPacket( player, subtitle );
        sendPacket( player, title );
        return this;
    }
}
