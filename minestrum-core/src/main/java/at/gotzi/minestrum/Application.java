package at.gotzi.minestrum;

import at.gotzi.api.ano.Comment;
import at.gotzi.api.logging.GLevel;
import at.gotzi.api.logging.GLogger;
import at.gotzi.minestrum.api.ArgumentStartable;
import at.gotzi.minestrum.utils.ShutdownTimer;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class Application  implements ArgumentStartable<String[]> {

    public static boolean DEBUG;

    private final Logger logger;
    protected Properties properties;
    private String[] args;

    @Comment.Constructor
    public Application() {
        this.logger = GLogger.getDefaultGotziLogger("main", true, true);
    }

    @Comment.Init
    @Override
    public void start(String[] strings) {
        final InputStream propertyStream = getClass().getClassLoader().getResourceAsStream("core.properties");

        this.logger.log(GLevel.Info, "Starting... Minestrum");

        this.logger.log(GLevel.Info, "Loading... Java Arguments");
        this.args = strings;

        this.logger.log(GLevel.Info, "Loading... Config");
        this.properties = new Properties();

        try {
            properties.load(propertyStream);
        } catch (Exception e) {
            this.logger.warning("Could not load Config -> " + e.getMessage());
            this.getLogger().log(GLevel.Warning, "Canceling Start...");
            this.getLogger().log(GLevel.Warning, "Application will shut down in 30-1");

            ShutdownTimer.startDefaultShutdown();
        }

        this.logger.log(GLevel.Info, "Logging Config");
        this.logProperties();

        Application.DEBUG =  Boolean.parseBoolean(this.properties.getProperty("debug"));
        this.start();
    }

    private void logProperties() {
        this.properties.entrySet().forEach(prop -> {
            if (prop.getValue().toString().charAt(0) != '$' || prop.getValue().toString().charAt(prop.getValue().toString().length()-1) != '$')
                this.logger.log(GLevel.Info, prop.toString());
            else {
                String str = prop.getKey() + "=" +
                        "*".repeat(prop.getValue().toString().length());
                this.logger.log(GLevel.Info, str);
            }
        });
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    @Comment.Getter
    public Logger getLogger() {
        return logger;
    }

    public abstract void start();
}