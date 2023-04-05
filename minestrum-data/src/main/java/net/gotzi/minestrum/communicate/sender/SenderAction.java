package net.gotzi.minestrum.communicate.sender;

public interface SenderAction {

    void send(String channel, byte[] data);

}
