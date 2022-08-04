package at.gotzi.minestrum.connection;

import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.api.logging.LogLevel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionHub {

    private final Minestrum minestrum;

    public ConnectionHub(Minestrum minestrum) {
        this.minestrum = minestrum;
    }

    public boolean checkConnection() {
        return ping("https://www.google.com/");
    }

    private boolean ping(String urlStr) {
        try {
            final URL url = new URL(urlStr);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            final long startTime = System.currentTimeMillis();
            conn.connect();
            final long endTime = System.currentTimeMillis();

            this.minestrum.getLogger().log(LogLevel.Info, "Google connected... Ping: " + (endTime-startTime-200));
            this.minestrum.getLogger().log(LogLevel.Info, "Closing Connection with Google");
            conn.getInputStream().close();

            return true;
        } catch (IOException e) {
            this.minestrum.getLogger().log(LogLevel.Warning, "Could not Connect to Google Servers, assume that System got no Internet");
            return false;
        }
    }


}
