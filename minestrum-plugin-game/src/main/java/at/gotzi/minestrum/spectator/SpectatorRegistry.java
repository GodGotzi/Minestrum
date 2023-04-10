package at.gotzi.minestrum.spectator;

import net.gotzi.minestrum.api.registry.Registry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpectatorRegistry implements Registry<UUID> {

    private final List<UUID> spectators;

    public SpectatorRegistry() {
        this.spectators = new ArrayList<>();
    }

    public boolean isGameSpectator(Player player) {
        return this.spectators.contains(player.getUniqueId());
    }

    public boolean isGameSpectator(UUID uuid) {
        return this.spectators.contains(uuid);
    }

    @Override
    public void register(UUID uuid) {
        spectators.add(uuid);
    }

    @Override
    public void unregister(UUID uuid) {
        spectators.remove(uuid);
    }
}
