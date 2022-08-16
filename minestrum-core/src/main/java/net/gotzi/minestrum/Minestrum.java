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
import net.gotzi.minestrum.discord.DiscordBot;
import net.gotzi.minestrum.email.EmailBot;
import net.gotzi.minestrum.error.ErrorHandler;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.logging.format.LogDefaultFormatter;
import net.gotzi.minestrum.logging.MinestrumLogger;
import net.gotzi.minestrum.connection.ConnectionHub;
import net.gotzi.minestrum.task.Task;
import net.gotzi.minestrum.task.TaskScheduler;
import net.gotzi.minestrum.terminal.TerminalHandler;
import net.gotzi.minestrum.utils.FileUtils;
import net.gotzi.minestrum.utils.MinestrumUtils;
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

    private final TerminalHandler terminalHandler;

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

        this.loadFolderStructure();

        this.taskHandler = new TaskScheduler();

        this.commandHandler = new CommandHandler(this.taskHandler,
                properties.getProperty("command_char").charAt(0));

        this.consoleReader = new ConsoleReader(System.in, System.out);
        this.prompt = properties.getProperty("console_prompt") + " ";
        this.consoleReader.addCompleter(this.commandHandler);
        this.consoleReader.setPrompt(this.prompt);

        Task task = new Task("command-handler", this::startCommandHandler);
        this.taskHandler.runRepeatingTask(task);

        this.terminalHandler = new TerminalHandler(
                consoleReader,
                new LogDefaultFormatter(true),
                new LogDefaultFormatter(false),
                loggingFolder
        );

        this.logger = MinestrumLogger.getConsoleLogger("main", this.terminalHandler);
        MinestrumUtils.LOGGER = this.logger;

        Minestrum.DEBUG =  Boolean.parseBoolean(this.properties.getProperty("debug"));
        ((MinestrumLogger)this.logger).setDebug(Minestrum.DEBUG);

        //System.setErr( new PrintStream( new LoggingOutputStream( logger, LogLevel.Error ), true ) );
        //System.setOut( new PrintStream( new LoggingOutputStream( logger, LogLevel.Info ), true ) );
    }

    public void start(String[] strings) {
        this.logger.log(LogLevel.Info, "Starting... Minestrum");

        this.logger.log(LogLevel.Info, "Loading... Java Arguments");
        this.args = strings;

        this.logger.log(LogLevel.Info, "Logging Config");
        this.logProperties();

        this.startup();
    }

    @Comment.Init
    public void startup() {
        this.commandHandler.setLogger(
                MinestrumLogger.getConsoleLogger("command-logger",  this.terminalHandler)
        );

        this.connectionHub = new ConnectionHub(this);
        this.logger.log(LogLevel.Info, "Checking for Connection...");

        if (!this.connectionHub.checkConnection()) {
            this.earlyShutdown();
        }

        this.logger.log(LogLevel.Important, "Internet confirmed");

        this.logger.log(LogLevel.Info, "Starting... Discord-Bot");

        LogDefaultFormatter botFormatter = new LogDefaultFormatter(false);

        this.discordBot = new DiscordBot(
                MinestrumLogger.getConsoleLogger("discord-logger", this.terminalHandler),
                botFormatter,
                this.properties
        ).start();

        this.logger.log(LogLevel.Important, "Discord-Bot ready");

        this.logger.log(LogLevel.Info, "Starting... Email-Bot");

        this.emailBot = new EmailBot(
                MinestrumLogger.getConsoleLogger("email-logger", this.terminalHandler),
                botFormatter,
                properties
        ).start();

        this.logger.log(LogLevel.Important, "Email-Bot ready");

        this.logger.log(LogLevel.Info, "Initialize ErrorHandler");

        this.errorHandler = new ErrorHandler(this,
                MinestrumLogger.getConsoleLogger("error-logger",  this.terminalHandler)
        );

        this.logger.log(LogLevel.Important, "ErrorHandler ready!");

        this.logger.log(LogLevel.Info, "Start BungeeCord");
        this.startBungee();

        this.logger.log(LogLevel.Info, "Register Commands");
        this.registerCommands();

        //Task task = new Task("Prompt Animation", promptAnimation::start);
        //taskHandler.runTask(task);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "Shutdown Thread"));
    }
    
    public void stop() {
        this.setShutdown(true);

        new Thread( "Shutdown Thread" ) {
            @Override
            public void run() {
                try {
                    FileUtils.clearTrashInFolder(errorFolder,
                            Integer.parseInt(properties.getProperty("dir_log_file_amount"))
                    );

                    FileUtils.clearTrashInFolder(loggingFolder,
                            Integer.parseInt(properties.getProperty("dir_log_file_amount"))
                    );

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
        //ShutdownTimer.startDefaultShutdown(this);
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

    public Bot getDiscordBot() {
        return discordBot;
    }

    public synchronized void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public synchronized boolean isRunning() {
        return !this.shutdown;
    }

    public Bot getEmailBot() {
        return emailBot;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public File getErrorFolder() {
        return errorFolder;
    }

    public static Minestrum getInstance() {
        return instance;
    }

    private static Minestrum instance;
}
