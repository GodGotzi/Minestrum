package net.gotzi.minestrum.connection;

import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import net.gotzi.minestrum.Bungee;
import net.gotzi.minestrum.BungeeServerInfo;
import net.gotzi.minestrum.api.Callback;
import net.gotzi.minestrum.api.ProxyServer;
import net.gotzi.minestrum.api.ServerPing;
import net.gotzi.minestrum.api.config.ServerInfo;
import net.gotzi.minestrum.netty.ChannelWrapper;
import net.gotzi.minestrum.netty.PacketHandler;
import net.gotzi.minestrum.netty.PipelineUtils;
import net.gotzi.minestrum.protocol.MinecraftDecoder;
import net.gotzi.minestrum.protocol.MinecraftEncoder;
import net.gotzi.minestrum.protocol.PacketWrapper;
import net.gotzi.minestrum.protocol.Protocol;
import net.gotzi.minestrum.protocol.packet.Handshake;
import net.gotzi.minestrum.protocol.packet.StatusRequest;
import net.gotzi.minestrum.protocol.packet.StatusResponse;
import net.gotzi.minestrum.util.BufUtil;
import net.gotzi.minestrum.util.QuietException;

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
