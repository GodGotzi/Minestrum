/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server;

import lombok.Getter;
import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.bungee.api.connection.ProxiedPlayer;
import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.SyncFuture;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.task.AsyncTaskScheduler;
import net.gotzi.minestrum.data.ServerType;
import net.gotzi.minestrum.task.Task;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;

public class ServerHandler {

    private final Minestrum minestrum;
    @Getter
    private final ServerRegistry<Server> registry;
    private final Properties properties;
    private final Logger logger;


    public ServerHandler(Properties properties, Logger logger, Minestrum minestrum) throws IOException {
        this.registry = new ServerRegistry<>();
        this.properties = properties;
        this.minestrum = minestrum;
        this.logger = logger;
    }

    public Server getServerByPort(int port) {
        return this.registry.getServerMap().get(port);
    }

    public void stopServer(Server server) {
        this.prepareStop(server);
        server.stop();

        Minestrum.getInstance().getLogger().log(LogLevel.FINE, "Server " + server.getPort() + " stopped!");
    }

    public void nextLobby(SyncFuture<Lobby> future) {
        this.getNextFreePort(new SyncFuture<>() {
            @Override
            public void done(Optional<Integer> portOptional) {
                if (portOptional.isEmpty()) {
                    //TODO Say that no more servers are available
                    return;
                }

                int port = portOptional.get();
                String name = "lobby-" + port;

                minestrum.getBungee().registerChannel(name);

                ServerInfo serverInfo = Bungee.getInstance().constructServerInfo(name,
                        new InetSocketAddress("localhost", port), name, false);

                ServerStartedFuture serverStartedFuture = () -> {
                    minestrum.getBungee().getServers().put("lobby-" + port, serverInfo);
                };

                String destination = ServerType.getDestinationFolder(ServerType.LOBBY, properties) + "//" + name + "//";
                File destFolder = new File(destination);

                File sourceFolder = new File(ServerType.getSourceFolder(ServerType.LOBBY, properties));

                Lobby lobby = new Lobby(sourceFolder, destFolder, port, serverStartedFuture);
                lobby.setServerInfo(serverInfo);
                registry.register(lobby);

                future.run(lobby);
            }

        });
    }

    public void nextServer(SyncFuture<PlayServer> future, int maxPlayers) {
        this.getNextFreePort(new SyncFuture<>() {
            @Override
            public void done(Optional<Integer> portOptional) {
                if (portOptional.isEmpty()) {
                    //TODO Say that no more servers are available
                    return;
                }

                int port = portOptional.get();
                String name = "play-" + port;

                minestrum.getBungee().registerChannel(name);

                ServerInfo serverInfo = Bungee.getInstance().constructServerInfo(name,
                        new InetSocketAddress("localhost", port), name, false);

                ServerStartedFuture serverStartedFuture = () -> {
                    minestrum.getBungee().getServers().put("lobby-" + port, serverInfo);
                };

                String destination = ServerType.getDestinationFolder(ServerType.PLAY, properties) + "//" + name + "//";
                File destFolder = new File(destination);

                File sourceFolder = new File(ServerType.getSourceFolder(ServerType.PLAY, properties));

                PlayServer playServer = new PlayServer(maxPlayers, sourceFolder, destFolder, port, serverStartedFuture);
                playServer.setServerInfo(playServer.getServerInfo());
                registry.register(playServer);

                future.run(playServer);
            }

        });
    }

    private static int MAX_PORT = 30000;
    private static int MIN_PORT = 25500;

    public void prepareStop(Server server) {
        for (ProxiedPlayer proxiedPlayer : server.getServerInfo().getPlayers()) {
            Lobby target = findNextLobby();

            proxiedPlayer.connect(target.getServerInfo());
        }

        getRegistry().unregister(server);
        minestrum.getBungee().getServers().remove(server.getName());
    }

    public Lobby findNextLobby() {
        Optional<Server> target = registry.getServers().stream().parallel().filter(s -> s.isLobby())
                .sorted(Comparator.comparingInt(s -> s.getServerInfo().getPlayers().size())).findFirst();

        try {
            return (Lobby) target.get();
        } catch (NoSuchElementException ex) {
            ErrorView errorView = new ErrorView("No Lobby server exits", ex);
            this.minestrum.getErrorHandler().registerError(errorView);
        }

        return null;
    }

    public Lobby findNextLobbyWithout(Server lobby) {
        Optional<Server> target = registry.getServers().stream().parallel().filter(s -> s.isLobby() && s == lobby)
                .sorted(Comparator.comparingInt(s -> s.getServerInfo().getPlayers().size())).findFirst();

        try {
            return (Lobby) target.get();
        } catch (NoSuchElementException ex) {
            ErrorView errorView = new ErrorView("No Lobby server exits", ex);
            this.minestrum.getErrorHandler().registerError(errorView);
        }

        return null;
    }

    public synchronized void rearrangeLobbyStructure() {
        //TODO rearrange Lobby structures but not needed
    }

    private void getNextFreePort(SyncFuture<Optional<Integer>> future) {
        AsyncTaskScheduler<Task> taskScheduler = minestrum.getTaskHandler();
        taskScheduler.runTask(new Task("port search", () -> {
            int port = MIN_PORT;
            synchronized (registry) {
                while (!registry.getServerMap().containsKey(port) && available(port)) {
                    ++port;
                }
            }

            //never happens
            if (port > MAX_PORT)
                future.run(Optional.empty());
            else
                future.run(Optional.of(port));

        }));
    }

    private boolean available(int port) {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }

        return false;
    }

    public void stopServers() {
        this.registry.getServers().forEach(this::stopServer);
    }

    public String constructProcessCommand(File serverDirectory, int ramMB) {
        String path = serverDirectory.getAbsolutePath();
        return String.format("java -Xmx%dM -Xms%dM -jar %s nogui", ramMB, ramMB, path + "/server.jar");
    }

    protected Properties getProperties() {
        return properties;
    }
}
