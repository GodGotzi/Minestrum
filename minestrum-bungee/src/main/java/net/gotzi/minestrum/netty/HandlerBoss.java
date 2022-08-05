package net.gotzi.minestrum.netty;

import net.gotzi.minestrum.api.logging.LogLevel;
import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.timeout.ReadTimeoutException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import net.gotzi.minestrum.connection.CancelSendSignal;
import net.gotzi.minestrum.connection.InitialHandler;
import net.gotzi.minestrum.connection.PingHandler;
import net.gotzi.minestrum.protocol.BadPacketException;
import net.gotzi.minestrum.protocol.OverflowPacketException;
import net.gotzi.minestrum.protocol.PacketWrapper;
import net.gotzi.minestrum.api.ProxyServer;
import net.gotzi.minestrum.util.QuietException;

/**
 * This class is a primitive wrapper for {@link PacketHandler} instances tied to
 * channels to maintain simple states, and only call the required, adapted
 * methods when the channel is connected.
 */
public class HandlerBoss extends ChannelInboundHandlerAdapter
{

    private ChannelWrapper channel;
    private PacketHandler handler;

    public void setHandler(PacketHandler handler)
    {
        Preconditions.checkArgument( handler != null, "handler" );
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        if ( handler != null )
        {
            channel = new ChannelWrapper( ctx );
            handler.connected( channel );

            if ( !( handler instanceof InitialHandler || handler instanceof PingHandler) )
            {
                ProxyServer.getInstance().getLogger().log(LogLevel.Info, "{0} has connected", handler );
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        if ( handler != null )
        {
            channel.markClosed();
            handler.disconnected( channel );

            if ( !( handler instanceof InitialHandler || handler instanceof PingHandler ) )
            {
                ProxyServer.getInstance().getLogger().log( LogLevel.Info, "{0} has disconnected", handler );
            }
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception
    {
        if ( handler != null )
        {
            handler.writabilityChanged( channel );
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if ( msg instanceof HAProxyMessage )
        {
            HAProxyMessage proxy = (HAProxyMessage) msg;
            try
            {
                if ( proxy.sourceAddress() != null )
                {
                    InetSocketAddress newAddress = new InetSocketAddress( proxy.sourceAddress(), proxy.sourcePort() );

                    ProxyServer.getInstance().getLogger().log( Level.FINE, "Set remote address via PROXY {0} -> {1}", new Object[]
                    {
                        channel.getRemoteAddress(), newAddress
                    } );

                    channel.setRemoteAddress( newAddress );
                }
            } finally
            {
                proxy.release();
            }
            return;
        }

        if ( handler != null )
        {
            PacketWrapper packet = (PacketWrapper) msg;
            boolean sendPacket = handler.shouldHandle( packet );
            try
            {
                if ( sendPacket && packet.packet != null )
                {
                    try
                    {
                        packet.packet.handle( handler );
                    } catch ( CancelSendSignal ex )
                    {
                        sendPacket = false;
                    }
                }
                if ( sendPacket )
                {
                    handler.handle( packet );
                }
            } finally
            {
                packet.trySingleRelease();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        if ( ctx.channel().isActive() )
        {
            boolean logExceptions = !( handler instanceof PingHandler );

            if ( logExceptions )
            {
                if ( cause instanceof ReadTimeoutException )
                {
                    ProxyServer.getInstance().getLogger().log( LogLevel.Warning, "{0} - read timed out", handler );
                } else if ( cause instanceof DecoderException )
                {
                    if ( cause instanceof CorruptedFrameException )
                    {
                        ProxyServer.getInstance().getLogger().log( LogLevel.Warning, "{0} - corrupted frame: {1}", new Object[]
                        {
                            handler, cause.getMessage()
                        } );
                    } else if ( cause.getCause() instanceof BadPacketException)
                    {
                        ProxyServer.getInstance().getLogger().log( LogLevel.Warning, "{0} - bad packet, are mods in use!? {1}", new Object[]
                        {
                            handler, cause.getCause().getMessage()
                        } );
                    } else if ( cause.getCause() instanceof OverflowPacketException)
                    {
                        ProxyServer.getInstance().getLogger().log( LogLevel.Warning, "{0} - overflow in packet detected! {1}", new Object[]
                        {
                            handler, cause.getCause().getMessage()
                        } );
                    } else
                    {
                        ProxyServer.getInstance().getLogger().log( LogLevel.Warning, "{0} - could not decode packet! {1}", new Object[]
                        {
                            handler, cause.getCause() != null ? cause.getCause() : cause
                        } );
                    }
                } else if ( cause instanceof IOException || ( cause instanceof IllegalStateException && handler instanceof InitialHandler ) )
                {
                    ProxyServer.getInstance().getLogger().log( LogLevel.Warning, "{0} - {1}: {2}", new Object[]
                    {
                        handler, cause.getClass().getSimpleName(), cause.getMessage()
                    } );
                } else if ( cause instanceof QuietException )
                {
                    ProxyServer.getInstance().getLogger().log( Level.SEVERE, "{0} - encountered exception: {1}", new Object[]
                    {
                        handler, cause
                    } );
                } else
                {
                    cause.printStackTrace();
                    //ProxyServer.getInstance().getLogger().log(LogLevel.Debug, cause);
                    ProxyServer.getInstance().getLogger().log( Level.SEVERE, handler + " - encountered exception", cause );
                }
            }

            if ( handler != null )
            {
                try
                {
                    handler.exception( cause );
                } catch ( Exception ex )
                {
                    ProxyServer.getInstance().getLogger().log( Level.SEVERE, handler + " - exception processing exception", ex );
                }
            }

            ctx.close();
        }
    }
}
