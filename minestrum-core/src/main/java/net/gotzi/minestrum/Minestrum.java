package net.gotzi.minestrum;

import jline.console.ConsoleReader;
import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.Bot;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.task.AsyncTaskScheduler;
import net.gotzi.minestrum.command.CommandHandler;
import net.gotzi.minestrum.command.CommandScanner;
import net.gotzi.minestrum.command.commands.ErrorCommand;
import net.gotzi.minestrum.command.commands.StopCommand;
import net.gotzi.minestrum.command.commands.VersionCommand;
import net.gotzi.minestrum.email.EmailBot;
import net.gotzi.minestrum.error.ErrorHandler;
import net.gotzi.minestrum.discord.DiscordBot;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.api.logging.MinestrumLogger;
import net.gotzi.minestrum.connection.ConnectionHub;
import net.gotzi.minestrum.task.Task;
import net.gotzi.minestrum.task.TaskScheduler;
import net.gotzi.minestrum.utils.FileUtils;
import net.gotzi.minestrum.utils.MinestrumUtils;
import net.gotzi.minestrum.utils.ShutdownTimer;
import net.gotzi.minestrum.api.BungeeFile;
import org.fusesource.jansi.AnsiConsole;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Minestrum {
    public static boolean DEBUG;

    private final Logger logger;
    private final ConsoleReader consoleReader;
    private final Properties properties;
    private final AsyncTaskScheduler<Task> taskHandler;
    private final CommandHandler commandHandler;
    private final String prompt;

    private String[] args;
    private Bot discordBot;
    private Bot emailBot;
    private ErrorHandler errorHandler;
    private ConnectionHub connectionHub;
    private Bungee bungee;
    private File errorFolder;
    private File loggingFolder;

    private boolean shutdown = false;

    public Minestrum() throws IOException {
        instance = this;
        System.setProperty( "library.jansi.version", "BungeeCord" );
        AnsiConsole.systemInstall();

        final InputStream propertyStream = getClass().getClassLoader()
                .getResourceAsStream("core.properties");
        this.properties = new Properties();
        properties.load(propertyStream);

        this.taskHandler = new TaskScheduler();

        this.commandHandler = new CommandHandler(this.taskHandler, '#');

        this.consoleReader = new ConsoleReader(System.in, System.out);
        this.prompt = properties.getProperty("console_prompt") + " ";
        this.consoleReader.addCompleter(this.commandHandler);
        this.consoleReader.setPrompt(this.prompt);

        Task task = new Task("command-handler", this::startCommandHandler);
        this.taskHandler.runRepeatingTask(task);

        this.logger = MinestrumLogger.getConsoleLogger("main", this.consoleReader, this.prompt);
        MinestrumUtils.LOGGER = this.logger;
    }

    public void start(String[] strings) {
        this.logger.log(LogLevel.Info, "Starting... Minestrum");

        this.logger.log(LogLevel.Info, "Loading... Java Arguments");
        this.args = strings;

        this.logger.log(LogLevel.Info, "Logging Config");
        this.logProperties();

        Minestrum.DEBUG =  Boolean.parseBoolean(this.properties.getProperty("debug"));
        ((MinestrumLogger)this.logger).setDebug(Minestrum.DEBUG);

        this.startup();
    }

    @Comment.Init
    public void startup() {
        this.loadFolderStructure();
        this.commandHandler.setLogger(
                MinestrumLogger.getConsoleLogger("command-logger", this.consoleReader, this.prompt)
        );

        this.connectionHub = new ConnectionHub(this);
        this.logger.log(LogLevel.Info, "Checking for Connection...");

        if (!this.connectionHub.checkConnection()) {
            this.earlyShutdown();
        }

        this.logger.log(LogLevel.Important, "Internet confirmed");

        this.logger.log(LogLevel.Info, "Starting... Discord-Bot");

        this.discordBot = new DiscordBot(
                MinestrumLogger.getConsoleLogger("discord-logger", this.consoleReader, this.prompt),
                this.properties
        ).start();

        this.logger.log(LogLevel.Important, "Discord-Bot ready");

        this.logger.log(LogLevel.Info, "Starting... Email-Bot");

        this.emailBot = new EmailBot(
                MinestrumLogger.getConsoleLogger("email-logger", this.consoleReader, this.prompt),
                properties).start();

        this.logger.log(LogLevel.Important, "Email-Bot ready");

        this.logger.log(LogLevel.Info, "Initialize ErrorHandler");

        this.errorHandler = new ErrorHandler(this,
                MinestrumLogger.getConsoleLogger("error-logger", this.consoleReader, this.prompt)
        );

        this.logger.log(LogLevel.Important, "ErrorHandler ready!");

        this.logger.log(LogLevel.Info, "Start BungeeCord");
        this.startBungee();

        this.logger.log(LogLevel.Info, "Register Commands");
        this.registerCommands();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "Shutdown Thread"));
    }
    
    public void stop() {
        this.setShutdown(true);

        new Thread( "Shutdown Thread" ) {
            @Override
            public void run() {
                try {
                    discordBot.stop();
                    bungee.stop();
                    taskHandler.stopTasks();
                } catch (Exception ignored) {
                    System.exit(0);
                }

                System.exit(0);
            }
        }.start();
    }

    public void earlyShutdown() {
        this.logger.log(LogLevel.Warning, "Canceling Start...");
        this.logger.log(LogLevel.Warning, "Application will shut down in 30-1");
        ShutdownTimer.startDefaultShutdown(this);
    }

    private void registerCommands() {
        this.commandHandler.registerCommand(new StopCommand("stop", this));
        this.commandHandler.registerCommand(new VersionCommand("version", this));
        this.commandHandler.registerCommand(new ErrorCommand("error", this));
    }

    private void startBungee() {
        try {
            BungeeFile.setFolder(this.properties.getProperty("bungee_dir"));
            this.bungee = new Bungee(this.logger, this.properties);
            this.bungee.start();
        } catch (Exception e) {
            this.errorHandler.registerError(new ErrorView("failed while starting Proxy", e));
        }
    }

    private void startCommandHandler() {
        CommandScanner commandScanner = () -> {
            try {
                return this.consoleReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        this.commandHandler.scanLoop(commandScanner);
    }

    private void loadFolderStructure() {
        FileUtils.initDir(this.properties.getProperty("bungee_dir"));

        String errorDir = this.properties.getProperty("error_dir");
        this.errorFolder = FileUtils.initDir(errorDir);

        String loggingDir = this.properties.getProperty("logging_dir");
        this.loggingFolder = FileUtils.initDir(loggingDir);
    }

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

    public Logger getLogger() {
        return logger;
    }

    public Properties getProperties() {
        return properties;
    }

    public AsyncTaskScheduler<Task> getTaskHandler() {
        return taskHandler;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public String[] getArgs() {
        return args;
    }

    public Bot getDiscordBot() {
        return discordBot;
    }

    public synchronized void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public synchronized boolean isShutdown() {
        return this.shutdown;
    }

    public Bot getEmailBot() {
        return emailBot;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public String getPrompt() {
        return prompt;
    }

    public ConnectionHub getConnectionHub() {
        return connectionHub;
    }

    public Bungee getBungee() {
        return bungee;
    }

    public File getErrorFolder() {
        return errorFolder;
    }

    public static Minestrum getInstance() {
        return instance;
    }

    private static Minestrum instance;
}
