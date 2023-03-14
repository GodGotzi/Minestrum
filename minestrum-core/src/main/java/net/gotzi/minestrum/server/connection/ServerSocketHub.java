/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server.connection;

import lombok.Getter;
import net.gotzi.minestrum.data.packet.Packet;
import net.gotzi.minestrum.server.Server;
import net.gotzi.minestrum.server.ServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

public class ServerSocketHub extends ServerSocket {

    @Getter
    private final ServerHandler serverHandler;
    private final PacketHandler packetHandler;
    private final Logger logger;

    public ServerSocketHub(int port, ServerHandler serverHandler, Logger logger) throws IOException {
        super(port);

        this.logger = logger;
        this.packetHandler = new PacketHandler(this, logger);
        this.serverHandler = serverHandler;
    }

    public void start() throws IOException {
        lookForNewSockets();

        for (Server server : serverHandler.getRegistry().getServers()) {
            SocketPipe pipe = server.getSocketPipe();
            Queue<Packet> packets = pipe.read();

            while (!packets.isEmpty()) {
                Packet packet = packets.poll();
                packetHandler.handle(packet, pipe);
            }
        }
    }

    private void lookForNewSockets() {
        synchronized (serverHandler.getRegistry().getServerMap()) {
            Socket socket = null;

            do {
                try {
                    socket = accept();
                    serverHandler.getRegistry().getServerMap().get(socket.getPort()).setSocketPipe(
                            new SocketPipe(socket, logger)
                    );
                } catch (IOException ignored) {}
            } while (socket != null);
        }
    }
}