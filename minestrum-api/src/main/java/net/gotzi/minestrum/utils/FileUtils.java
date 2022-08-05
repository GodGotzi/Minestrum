package net.gotzi.minestrum.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void initFile(File file) {
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File initFile(String path) {
        File file = new File(path);

        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return file;
        }

        return file;
    }

    public static File initDir(String path) {
        File file = new File(path);
        file.mkdirs();
        return file;
    }

    public static void initDir(File file) {
        file.mkdirs();
    }
}
