package net.gotzi.minestrum.api;

import java.io.File;

public class BungeeFile extends File {

    public static String folder;

    public BungeeFile(String pathname) {
        super(folder + "/" +pathname);
    }

    public static void setFolder(String folder) {
        BungeeFile.folder = folder;
    }
}