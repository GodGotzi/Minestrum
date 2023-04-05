package at.gotzi.minestrum;

import at.gotzi.minestrum.communicate.ProxyListener;
import net.gotzi.minestrum.communicate.receiver.PacketReceiver;
import net.gotzi.minestrum.communicate.sender.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Game extends JavaPlugin {

    private PacketReceiver packetReceiver;
    private PacketSender packetSender;

    public Game() {
        this.packetReceiver = new PacketReceiver();
        this.packetSender = new PacketSender((channel, data) -> {
            getServer().sendPluginMessage(this, channel, data);
        }, packetReceiver);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ProxyListener proxyListener = new ProxyListener(packetReceiver);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "game:" + this.getServer().getPort());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "game:", proxyListener);

        Bukkit.getScheduler().runTaskLater(this, () -> {

        }, 5000);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);


    }
}