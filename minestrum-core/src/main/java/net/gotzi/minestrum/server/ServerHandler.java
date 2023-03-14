/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server;

import lombok.Getter;
import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.data.ServerType;
import net.gotzi.minestrum.data.packet.Packet;
import net.gotzi.minestrum.data.packet.StopPacket;
import net.gotzi.minestrum.server.connection.ServerSocketHub;
import net.gotzi.minestrum.server.servers.Lobby;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ServerHandler {

    private final ServerSocketHub serverHub;
    @Getter
    private final ServerRegistry<? extends Server> registry;
    @Getter
    private final Properties properties;

    @Getter
    private Server lobby;

    public ServerHandler(Properties properties, Logger socketLogger) throws IOException {
        this.serverHub = new ServerSocketHub(
                Integer.parseInt(properties.getProperty("server.hub.port")),
                this,
                socketLogger
        );

        this.registry = new ServerRegistry<>();
        this.properties = properties;
    }

    public void startLobbyServer() {
        try {
            this.lobby = Lobby.startServer(this);
        } catch (IOException e) {
            ErrorView view = new ErrorView("error starting lobby", e);
            Minestrum.getInstance().getErrorHandler().registerError(view);
        }

        //TODO configure Lobby
    }

    public Server getServerByPort(int port) {
        return this.registry.getServerMap().get(port);
    }

    public void stopServer(Server server) {
        try {
            if (server.getSocketPipe() != null) {
                Packet<StopPacket> packet = new StopPacket(ServerType.PROXY,
                        ServerType.getLobbyPort(), true);

                server.getSocketPipe().write(
                        packet
                );
            } else {

                Process process = server.getProcess();
                new OutputStreamWriter(process.getOutputStream()).write("stop");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getNextFreePort() {
        int port = 25581;

        while (registry.getServerMap().containsKey(port)) {
            ++port;
        }

        return port;
    }

    public void stopServers() {
        this.registry.getServers().forEach(this::stopServer);
    }
}
