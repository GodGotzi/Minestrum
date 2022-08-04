package at.gotzi.minestrum;

import at.gotzi.minestrum.ano.Comment;
import at.gotzi.minestrum.api.ArgumentStartable;
import at.gotzi.minestrum.api.logging.LogLevel;
import at.gotzi.minestrum.api.logging.MinestrumLogger;
import at.gotzi.minestrum.utils.MinestrumUtils;
import jline.WindowsTerminal;
import jline.console.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class Application  implements ArgumentStartable<String[]> {

    public static boolean DEBUG;
    private final Logger logger;
    private Properties properties;
    private ConsoleReader consoleReader;
    private String[] args;

    @Comment.Constructor
    public Application() {
        System.setProperty( "library.jansi.version", "BungeeCord" );
        AnsiConsole.systemInstall();

        this.logger = MinestrumLogger.getDefaultGotziLogger("main", true, true);
        MinestrumUtils.LOGGER = this.logger;
    }

    @Comment.Init
    @Override
    public void start(String[] strings) throws IOException {
        final InputStream propertyStream = getClass().getClassLoader().getResourceAsStream("core.properties");

        this.logger.log(LogLevel.Info, "Starting... Minestrum");

        this.logger.log(LogLevel.Info, "Loading... Java Arguments");
        this.args = strings;

        this.logger.log(LogLevel.Info, "Loading... Config");
        this.properties = new Properties();

        try {
            properties.load(propertyStream);
        } catch (Exception e) {
            this.logger.warning("Could not load Config -> " + e.getMessage());
            earlyShutdown();
        }

        this.logger.log(LogLevel.Info, "Logging Config");
        this.logProperties();


        Application.DEBUG =  Boolean.parseBoolean(this.properties.getProperty("debug"));
        ((MinestrumLogger)this.logger).setDebug(Application.DEBUG);

        this.start();
    }

    public abstract void earlyShutdown();

    public abstract void stop();

    private void logProperties() {
        this.properties.entrySet().forEach(prop -> {
            if (prop.getValue().toString().charAt(0) != '$' || prop.getValue().toString().charAt(prop.getValue().toString().length()-1) != '$')
                this.logger.log(LogLevel.Info, prop.toString());
            else {
                String str = prop.getKey() + "=" +
                        "*".repeat(prop.getValue().toString().length());
                this.logger.log(LogLevel.Info, str);
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

    public String[] getArgs() {
        return args;
    }

    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }

    public Properties getProperties() {
        return properties;
    }

    public abstract void start() throws IOException;
}
