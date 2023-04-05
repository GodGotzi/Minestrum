package at.gotzi.minestrum;

import at.gotzi.minestrum.communicate.ProxyListener;
import net.gotzi.minestrum.communicate.receiver.PacketReceiver;
import net.gotzi.minestrum.communicate.sender.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;

public class Lobby extends JavaPlugin {

    private PacketReceiver packetReceiver;
    private PacketSender packetSender;
    private String channel;

    public Lobby() {
        this.packetReceiver = new PacketReceiver();
        this.packetSender = new PacketSender((channel, data) -> {
            getServer().sendPluginMessage(this, channel, data);
        }, packetReceiver);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.channel = "game:" + this.getServer().getPort();

        ProxyListener proxyListener = new ProxyListener(packetReceiver);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, this.channel);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, this.channel, proxyListener);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            String data = "booted";
            packetSender.sendData(this.channel, data.getBytes(StandardCharsets.UTF_8));
        }, 1000);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);


    }
}