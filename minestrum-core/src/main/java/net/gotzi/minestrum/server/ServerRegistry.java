package net.gotzi.minestrum.server;

import net.gotzi.minestrum.api.registry.Registry;
import net.gotzi.spigot.FakeServer;

import java.util.*;

public class ServerRegistry<S extends FakeServer> implements Registry<S> {

    private final Map<UUID, S> servers = new LinkedHashMap<>();

    public ServerRegistry() {
    }

    @Override
    public void register(S server) {
        servers.put(server.getData().getID(), server);

    }

    @Override
    public void unregister(S server) {
        servers.remove(server.getData().getID(), server);
    }

    public void unregister(UUID id) {
        servers.remove(id);
    }

    public Map<UUID, S> getServerMap() {
        return servers;
    }

    public Collection<S> getServers() {
        return servers.values();
    }
}
