package at.gotzi.minestrumbungee;

import java.io.File;

public class BungeeFile extends File {

    public BungeeFile(String str) {
        super(MinestrumBungee.getProperties()
                .getProperty("bungee_dir") + "/" + str);
    }
}
