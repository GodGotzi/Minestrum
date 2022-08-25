package net.gotzi.minestrum.server;

import net.gotzi.spigot.FakeServer;

import java.util.UUID;

public class ServerHandler {

    private final ServerRegistry<? extends FakeServer> registry;

    public ServerHandler() {
        this.registry = new ServerRegistry<>();
    }

    public ServerRegistry<? extends FakeServer> getRegistry() {
        return registry;
    }

    public void stopServer(UUID uuid) {
        this.registry.getServerMap().get(uuid).stop();
    }

    public void stopServers() {
        this.registry.getServers().forEach(FakeServer::stop);
    }
}
