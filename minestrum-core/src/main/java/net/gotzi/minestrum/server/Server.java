package net.gotzi.minestrum.server;

import lombok.Getter;
import lombok.Setter;
import net.gotzi.minestrum.server.connection.SocketPipe;

public class Server {

    @Getter
    private final ServerStartedFuture future;
    @Getter
    private final Process process;
    @Getter
    private final int port;

    @Getter
    @Setter
    private SocketPipe socketPipe = null;

    public Server(int port, Process process, ServerStartedFuture future) {
        this.port = port;
        this.process = process;
        this.future = future;
    }
}
