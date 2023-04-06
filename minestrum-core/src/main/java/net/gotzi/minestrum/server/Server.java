/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.server;

import lombok.Getter;
import lombok.Setter;
import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.api.config.ServerInfo;
import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.error.ErrorView;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

public abstract class Server {

    @Getter
    private final ServerStartedFuture future;
    @Getter
    private final int port;

    @Getter
    private final int maxPlayers;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private ServerInfo serverInfo;

    @Getter
    private Process process;

    public Server(int maxPlayers, File source, File destination, int port, ServerStartedFuture future) {
        this.port = port;
        this.future = future;
        this.maxPlayers = maxPlayers;

        try {
            copyFolder(source, destination);
            editConfigForNeededPort(destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String cmd = constructCommand(destination);
        try {
            this.process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            ErrorView view = new ErrorView("error while starting server", e);
            Minestrum.getInstance().getErrorHandler().registerError(view);
        }
    }

    private String constructCommand(File destination) {
        String path = destination.getAbsolutePath();
        return String.format("java -jar %s nogui", path + "/server.jar");
    }

    private void editConfigForNeededPort(File destination) throws IOException {
        FileReader reader = new FileReader(destination.getAbsolutePath() + "/server.properties");

        Properties properties = new Properties();
        properties.load(reader);
        reader.close();

        properties.setProperty("query.port", String.valueOf(this.port));
        properties.setProperty("server-port", String.valueOf(this.port));


        BufferedWriter writer = new BufferedWriter(
                new FileWriter(destination.getAbsolutePath() + "/server.properties")
        );

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            writer.write(entry.getKey() + "=" + entry.getValue());
            writer.newLine();
        }

        writer.flush();
        writer.close();
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
