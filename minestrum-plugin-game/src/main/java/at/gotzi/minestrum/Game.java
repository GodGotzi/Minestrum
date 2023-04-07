package at.gotzi.minestrum;

import at.gotzi.minestrum.api.MinestrumPlayerInfo;
import at.gotzi.minestrum.communicate.ProxyListener;
import at.gotzi.minestrum.config.MessageConfig;
import at.gotzi.minestrum.config.format.FormatType;
import at.gotzi.minestrum.config.format.FormatValue;
import at.gotzi.minestrum.spectator.SpectatorHandler;
import at.gotzi.minestrum.state.GameStateHandler;
import at.gotzi.minestrum.api.PlayerInfo;
import at.gotzi.minestrum.state.PrimaryGameState;
import lombok.Getter;
import net.gotzi.minestrum.communicate.receiver.PacketReceiver;
import net.gotzi.minestrum.communicate.sender.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Game extends JavaPlugin {

    private final Map<UUID, PlayerInfo> playerInfoMap;

    @Getter
    private final MessageConfig messageConfig;

    @Getter
    private final PacketReceiver packetReceiver;

    @Getter
    private final PacketSender packetSender;

    @Getter
    private final GameStateHandler stateHandler;

    @Getter
    private final SpectatorHandler spectatorHandler;

    public Game() {
        this.playerInfoMap = new HashMap<>();
        this.messageConfig = new MessageConfig();
        this.packetReceiver = new PacketReceiver();
        this.packetSender = new PacketSender((channel, data) -> {
            getServer().sendPluginMessage(this, channel, data);
        }, packetReceiver);
        this.stateHandler = new GameStateHandler(PrimaryGameState.IDLE, this);
        this.spectatorHandler = new SpectatorHandler();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        ProxyListener proxyListener = new ProxyListener(packetReceiver);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "game:" + this.getServer().getPort());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "game:", proxyListener);

        this.getServer().getPluginManager().registerEvents(new PlayerCoordinator(this), this);

        Bukkit.getScheduler().runTaskLater(this, () -> {

        }, 5000);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);


    }

    public PlayerInfo getPlayerInfo(Player player) {
        return playerInfoMap.get(player.getUniqueId());
    }

    protected void registerPlayer(Player player) {
        playerInfoMap.put(player.getUniqueId(), new MinestrumPlayerInfo(this, player));
    }

    protected void unregisterPlayer(Player player) {
        playerInfoMap.remove(player.getUniqueId());
    }
}