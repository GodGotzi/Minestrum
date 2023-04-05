/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server;

import lombok.Getter;
import lombok.Setter;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.error.ErrorView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class Server {

    @Getter
    private final ServerStartedFuture future;
    @Getter
    private final int port;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private ServerInfo serverInfo;

    @Getter
    private Process process;

    public Server(File source, File destination, int ramMB, int port, ServerStartedFuture future) {
        this.port = port;
        this.future = future;

        try {
            copyFolder(source, destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String cmd = constructCommand(destination, ramMB);
        try {
            this.process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            ErrorView view = new ErrorView("error while starting server", e);
            Minestrum.getInstance().getErrorHandler().registerError(view);
        }
    }

    private String constructCommand(File destination, int ramMB) {
        String path = destination.getAbsolutePath();
        return String.format("java -Xmx%dM -Xms%dM -jar %s nogui", ramMB, ramMB, path + "/server.jar");
    }

    private void copyFolder(File sourceFolder, File destFolder) throws IOException {
        if (!sourceFolder.exists()) {
            throw new IllegalArgumentException("Source folder does not exist.");
        }
        if (!destFolder.exists()) {
            destFolder.mkdir();
        }
        if (!destFolder.isDirectory()) {
            throw new IllegalArgumentException("Destination folder is not a directory.");
        }
        File[] files = sourceFolder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                copyFolder(file, new File(destFolder.getAbsolutePath() + "/" + file.getName()));
            } else {
                Files.copy(file.toPath(), new File(destFolder.getAbsolutePath() + "/" + file.getName()).toPath());
            }
        }
    }

    public synchronized void booted() {
        this.future.run();
    }

    public synchronized void stop() {

    }

    public abstract boolean isLobby();

}
