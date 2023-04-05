package net.gotzi.minestrum.listener;

import net.gotzi.bungee.api.event.PluginMessageEvent;
import net.gotzi.bungee.api.plugin.Listener;
import net.gotzi.bungee.event.EventHandler;
import net.gotzi.minestrum.communicate.receiver.PacketReceiver;

public class ServerListener implements Listener {

    private final PacketReceiver packetReceiver;

    public ServerListener(PacketReceiver packetReceiver) {
        this.packetReceiver = packetReceiver;
    }

    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        String data = new String(event.getData());
        packetReceiver.executeCommand(data, event.getTag());
    }

}
