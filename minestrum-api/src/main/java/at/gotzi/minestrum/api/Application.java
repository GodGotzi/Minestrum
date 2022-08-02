package at.gotzi.minestrum.api;

import at.gotzi.api.GHelper;
import at.gotzi.api.ano.Comment;
import at.gotzi.api.command.CommandHandler;
import at.gotzi.api.logging.GLevel;
import at.gotzi.api.logging.GLogger;
import jline.console.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class Application  implements ArgumentStartable<String[]> {

    public static boolean DEBUG;
    private final Logger logger;
    private final List<ConsoleReader> consoleReaders;
    private CommandHandler commandHandler;
    private Properties properties;
    private String[] args;

    @Comment.Constructor
    public Application() {
        System.setProperty( "library.jansi.version", "BungeeCord" );
        AnsiConsole.systemInstall();

        this.logger = GLogger.getDefaultGotziLogger("main", true, true);
        GHelper.LOGGER = (GLogger) this.logger;

        this.consoleReaders = new ArrayList<>();
    }

    @Comment.Init
    @Override
    public void start(String[] strings) throws IOException {
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
            earlyShutdown();
        }

        this.logger.log(GLevel.Info, "Logging Config");
        this.logProperties();

        Application.DEBUG =  Boolean.parseBoolean(this.properties.getProperty("debug"));
        ((GLogger)this.logger).setDebug(Application.DEBUG);

        this.logger.log(GLevel.Info, "Loading ConsoleReader");
    }

    public abstract void earlyShutdown();

    public abstract void stop();

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

    public List<ConsoleReader> getConsoleReaders() {
        return consoleReaders;
    }

    public synchronized CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public String[] getArgs() {
        return args;
    }

    public Properties getProperties() {
        return properties;
    }

    public abstract void start() throws IOException;
}
