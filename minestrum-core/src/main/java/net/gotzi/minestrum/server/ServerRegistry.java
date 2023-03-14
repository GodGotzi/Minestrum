/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server;

import net.gotzi.minestrum.api.registry.Registry;

import java.util.*;

public class ServerRegistry<S extends Server> implements Registry<S> {

    private final SortedMap<Integer, S> servers = new TreeMap<>();

    public ServerRegistry() {
    }

    @Override
    public void register(S server) {
        servers.put(server.getPort(), server);
    }

    @Override
    public void unregister(S server) {
        servers.remove(server.getPort(), server);
    }

    public void unregister(int id) {
        servers.remove(id);
    }

    public SortedMap<Integer, S> getServerMap() {
        return servers;
    }

    public S getServer(int id) {
        return servers.get(id);
    }

    public Collection<S> getServers() {
        return servers.values();
    }
}
