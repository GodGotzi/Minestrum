package net.gotzi.minestrum.data.packet;

import net.gotzi.minestrum.data.PacketType;
import net.gotzi.minestrum.data.ServerType;

import java.nio.ByteBuffer;

public abstract class Packet<P extends Packet<?>> {

    private PacketType packetType;
    private ServerType serverType;
    private int serverPort;
    private boolean backRequired;

    protected void constructDefault(ByteBuffer buffer) {
        boolean backRequired = buffer.get(0) == 1;
        int serverPort = buffer.getInt(1);
        int serverType = buffer.getInt(6);

        this.setBackRequired(backRequired);
        this.setServerPort(serverPort);
        this.setServerType(ServerType.values()[serverType]);
    }

    public abstract byte[] toBytes();

    public PacketType getPacketType() {
        return packetType;
    }

    public boolean isBackRequired() {
        return backRequired;
    }

    public int getServerPort() {
        return serverPort;
    }

    public ServerType getServerType() {
        return serverType;
    }

    protected void setBackRequired(boolean backRequired) {
        this.backRequired = backRequired;
    }

    protected void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    protected void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    protected void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
