package net.gotzi.minestrum.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import net.gotzi.minestrum.api.Callback;

@RequiredArgsConstructor
public class HttpHandler extends SimpleChannelInboundHandler<HttpObject>
{

    private final Callback<String> callback;
    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        try
        {
            callback.done( null, cause );
        } finally
        {
            ctx.channel().pipeline().remove( this );
            ctx.channel().close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception
    {
        if ( msg instanceof HttpResponse response)
        {
            int responseCode = response.status().code();

            if ( responseCode == HttpResponseStatus.NO_CONTENT.code() )
            {
                done( ctx );
                return;
            }

            if ( responseCode != HttpResponseStatus.OK.code() )
            {
                throw new IllegalStateException( "Expected HTTP response 200 OK, got " + response.status() );
            }
        }
        if (msg instanceof HttpContent content)
        {
            buffer.append( content.content().toString(StandardCharsets.UTF_8) );

            if ( msg instanceof LastHttpContent )
            {
                done( ctx );
            }
        }
    }

    private void done(ChannelHandlerContext ctx)
    {
        try
        {
            callback.done( buffer.toString(), null );
        } finally
        {
            ctx.channel().pipeline().remove( this );
            ctx.channel().close();
        }
    }
}
