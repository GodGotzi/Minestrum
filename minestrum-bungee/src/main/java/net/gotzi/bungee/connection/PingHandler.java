package net.gotzi.bungee.connection;

import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.BungeeServerInfo;
import net.gotzi.bungee.api.Callback;
import net.gotzi.bungee.api.ProxyServer;
import net.gotzi.bungee.api.ServerPing;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.bungee.netty.ChannelWrapper;
import net.gotzi.bungee.netty.PacketHandler;
import net.gotzi.bungee.netty.PipelineUtils;
import net.gotzi.bungee.protocol.MinecraftDecoder;
import net.gotzi.bungee.protocol.MinecraftEncoder;
import net.gotzi.bungee.protocol.PacketWrapper;
import net.gotzi.bungee.protocol.Protocol;
import net.gotzi.bungee.protocol.packet.Handshake;
import net.gotzi.bungee.protocol.packet.StatusRequest;
import net.gotzi.bungee.protocol.packet.StatusResponse;
import net.gotzi.bungee.util.BufUtil;
import net.gotzi.bungee.util.QuietException;

@RequiredArgsConstructor
public class PingHandler extends PacketHandler
{

    private final ServerInfo target;
    private final Callback<ServerPing> callback;
    private final int protocol;
    private ChannelWrapper channel;

    @Override
    public void connected(ChannelWrapper channel) throws Exception
    {
        this.channel = channel;
        MinecraftEncoder encoder = new MinecraftEncoder( Protocol.HANDSHAKE, false, protocol );

        channel.getHandle().pipeline().addAfter( PipelineUtils.FRAME_DECODER, PipelineUtils.PACKET_DECODER, new MinecraftDecoder( Protocol.STATUS, false, ProxyServer.getInstance().getProtocolVersion() ) );
        channel.getHandle().pipeline().addAfter( PipelineUtils.FRAME_PREPENDER, PipelineUtils.PACKET_ENCODER, encoder );

        channel.write( new Handshake( protocol, target.getAddress().getHostString(), target.getAddress().getPort(), 1 ) );

        encoder.setProtocol( Protocol.STATUS );
        channel.write( new StatusRequest() );
    }

    @Override
    public void exception(Throwable t) throws Exception
    {
        callback.done( null, t );
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception
    {
        if ( packet.packet == null )
        {
            throw new QuietException( "Unexpected packet received during ping process! " + BufUtil.dump( packet.buf, 16 ) );
        }
    }

    @Override
    @SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
    public void handle(StatusResponse statusResponse) throws Exception
    {
        Gson gson = Bungee.getInstance().gson;
        ServerPing serverPing = gson.fromJson( statusResponse.getResponse(), ServerPing.class );
        ( (BungeeServerInfo) target ).cachePing( serverPing );
        callback.done( serverPing, null );
        channel.close();
    }

    @Override
    public String toString()
    {
        return "[Ping Handler] -> " + target.getName();
    }
}
