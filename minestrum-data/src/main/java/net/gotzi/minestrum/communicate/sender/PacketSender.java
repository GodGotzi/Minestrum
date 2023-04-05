package net.gotzi.minestrum.communicate.sender;

import net.gotzi.minestrum.communicate.receiver.PacketReceiver;

import java.nio.charset.StandardCharsets;

public class PacketSender {

    private final SenderAction senderAction;
    private final PacketReceiver packetReceiver;

    public PacketSender(SenderAction senderAction, PacketReceiver packetReceiver) {
        this.senderAction = senderAction;
        this.packetReceiver = packetReceiver;
    }

    public void sendData(String channel, byte[] data) {
        this.senderAction.send(channel, data);
    }

    public void sendData(String channel, String data) {
        this.senderAction.send(channel, data.getBytes(StandardCharsets.UTF_8));
    }



}
