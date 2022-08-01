package at.gotzi.minestrum;

import at.gotzi.api.GHelper;
import at.gotzi.api.ano.Comment;
import at.gotzi.api.command.CommandHandler;
import at.gotzi.api.template.logging.GLevel;
import at.gotzi.api.template.logging.GLogger;
import at.gotzi.minestrum.task.AsyncTaskHandler;
import at.gotzi.minestrum.api.ArgumentStartable;
import at.gotzi.minestrum.task.MinestrumTaskHandler;
import at.gotzi.minestrum.task.Task;
import at.gotzi.minestrum.utils.ShutdownTimer;
import jline.console.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

public abstract class Application  implements ArgumentStartable<String[]> {

    public static boolean DEBUG;

    private final Logger logger;
    private final List<ConsoleReader> consoleReaders;
    private final AsyncTaskHandler taskHandler;
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
        this.taskHandler = new MinestrumTaskHandler();
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
            this.getLogger().log(GLevel.Warning, "Canceling Start...");
            this.getLogger().log(GLevel.Warning, "Application will shut down in 30-1");

            ShutdownTimer.startDefaultShutdown();
        }

        this.logger.log(GLevel.Info, "Logging Config");
        this.logProperties();

        Application.DEBUG =  Boolean.parseBoolean(this.properties.getProperty("debug"));
        ((GLogger)this.logger).setDebug(Application.DEBUG);

        this.commandHandler = new CommandHandler(this.properties.getProperty("command_char").charAt(0));
        Task task = new Task("cmd-handler", this::startCommandHandler);
        this.taskHandler.runTask(task);

        this.logger.log(GLevel.Info, "Loading ConsoleReader");

        this.start();
    }

    public abstract void stop();

    private void startCommandHandler() {
        this.logger.log(GLevel.Info, "Loading CommandHandler");
        Scanner scanner = new Scanner(System.in);
        this.commandHandler.scanLoop(scanner::nextLine);
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

    public List<ConsoleReader> getConsoleReaders() {
        return consoleReaders;
    }

    public synchronized CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public AsyncTaskHandler getTaskHandler() {
        return taskHandler;
    }

    public String[] getArgs() {
        return args;
    }

    public Properties getProperties() {
        return properties;
    }

    public abstract void start() throws IOException;
}
