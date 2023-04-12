package at.gotzi.minestrum;

import at.gotzi.minestrum.api.MinestrumGamePlayer;
import at.gotzi.minestrum.communicate.ProxyListener;
import at.gotzi.minestrum.config.MessageConfig;
import at.gotzi.minestrum.config.dynamic.LocationConfig;
import at.gotzi.minestrum.config.dynamic.RandomspawnConfig;
import at.gotzi.minestrum.config.format.FormatType;
import at.gotzi.minestrum.config.format.FormatValue;
import at.gotzi.minestrum.listener.player.PlayerListener;
import at.gotzi.minestrum.listener.player.PlayerListenerAdapter;
import at.gotzi.minestrum.spectator.SpectatorRegistry;
import at.gotzi.minestrum.state.GameStateHandler;
import at.gotzi.minestrum.api.GamePlayer;
import at.gotzi.minestrum.state.GameState;
import lombok.Getter;
import net.gotzi.minestrum.communicate.receiver.PacketReceiver;
import net.gotzi.minestrum.communicate.sender.PacketSender;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Game extends JavaPlugin {

    private final Map<UUID, GamePlayer> playerInfoMap;

    @Getter
    private final MessageConfig messageConfig;

    @Getter
    private final PacketReceiver packetReceiver;

    @Getter
    private final PacketSender packetSender;

    @Getter
    private final GameStateHandler stateHandler;

    @Getter
    private final SpectatorRegistry spectatorRegistry;

    @Getter
    private final LocationCoordinator locationCoordinator;

    public Game() {
        this.playerInfoMap = new HashMap<>();
        this.messageConfig = new MessageConfig();
        this.packetReceiver = new PacketReceiver();
        this.packetSender = new PacketSender((channel, data) -> {
            getServer().sendPluginMessage(this, channel, data);
        }, packetReceiver);
        this.stateHandler = new GameStateHandler(GameState.IDLE, this);
        this.spectatorRegistry = new SpectatorRegistry();

        LocationConfig locationConfig = new LocationConfig("locations.properties");
        RandomspawnConfig randomspawnConfig = new RandomspawnConfig("random-spawn-locations.properties");

        this.locationCoordinator = new LocationCoordinator(locationConfig, randomspawnConfig);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        PlayerListener playerListener = new PlayerListener();
        ProxyListener proxyListener = new ProxyListener(packetReceiver);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "game:" + this.getServer().getPort());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "game:", proxyListener);
        this.getServer().getPluginManager().registerEvents(playerListener, this);

        playerListener.register(this.stateHandler);
        playerListener.register(new PlayerConnectionListener(this));

        Bukkit.getScheduler().runTaskLater(this, () -> {

        }, 5000);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public int getMaxGamePlayers() {
        return Integer.parseInt(((DedicatedServer) MinecraftServer.getServer()).getDedicatedServerProperties()
                .properties.getProperty("max-game-players"));
    }

    public GamePlayer getPlayerInfo(Player player) {
        return playerInfoMap.get(player.getUniqueId());
    }

    public void registerPlayer(Player player) {
        playerInfoMap.put(player.getUniqueId(), new MinestrumGamePlayer(this, player));
    }

    public void unregisterPlayer(Player player) {
        playerInfoMap.remove(player.getUniqueId());
    }

    private class PlayerConnectionListener extends PlayerListenerAdapter {

        private final Game game;

        private PlayerConnectionListener(Game game) {
            this.game = game;
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            this.game.registerPlayer(event.getPlayer());
            boolean full = true;

            if (!full) {
                event.setJoinMessage(game.getMessageConfig().getMessage(
                        "join.message", new FormatValue(FormatType.PLAYER, event.getPlayer().getName())
                ));

                Bukkit.getScheduler().runTaskLater(game, () -> {
                    this.game.getStateHandler().start();
                }, 20);
            } else {
                event.setJoinMessage("");

            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            event.setQuitMessage(game.getMessageConfig().getMessage(
                    "quit.message", new FormatValue(FormatType.PLAYER, event.getPlayer().getName())
            ));

            Bukkit.getScheduler().runTaskLater(game, () -> {
                this.game.getStateHandler().cancelCountdown();

                this.game.unregisterPlayer(event.getPlayer());
            }, 20);
        }
    }
}