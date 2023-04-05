package at.gotzi.minestrum.communicate;

import net.gotzi.minestrum.communicate.receiver.PacketReceiver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ProxyListener implements PluginMessageListener {

    private final PacketReceiver packetReceiver;

    public ProxyListener(PacketReceiver packetReceiver) {
        this.packetReceiver = packetReceiver;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        String msg = new String(message);

        this.packetReceiver.executeCommand(msg, channel);
    }
}
