package at.gotzi.minestrum.utils;

import at.gotzi.api.template.logging.GLevel;
import at.gotzi.minestrum.Minestrum;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionHelper {

    public static boolean ping(String urlStr) {
        try {
            final URL url = new URL(urlStr);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            final long startTime = System.currentTimeMillis();
            conn.connect();
            final long endTime = System.currentTimeMillis();

            Minestrum.getInstance().getLogger().log(GLevel.Info, "Google connected... Ping: " + (endTime-startTime-200));
            Minestrum.getInstance().getLogger().log(GLevel.Info, "Closing Connection with Google");
            conn.getInputStream().close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }


}
