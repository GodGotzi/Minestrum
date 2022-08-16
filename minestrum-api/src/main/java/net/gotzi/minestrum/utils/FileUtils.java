package net.gotzi.minestrum.utils;

import net.gotzi.minestrum.api.logging.LogLevel;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

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

    public static void clearTrashInFolder(File file, int amount) {
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });

        if (file == null) return;
        if (files.length == 0) return;

        Arrays.sort(files, Comparator.comparing(File::lastModified));

        for (int i = 0; i < files.length - amount; i++) {
            boolean bool = files[i].delete();
            if (!bool) MinestrumUtils.LOGGER.log(LogLevel.Warning,
                    "Can't delete last modified trash File {0}", files[i].getPath()
            );
            else MinestrumUtils.LOGGER.log(LogLevel.Fine,
                    "Deleted trash file {0}", files[i].getPath()
            );
        }
    }
}