package net.gotzi.minestrum;

import jline.console.ConsoleReader;
import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.Bot;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.task.AsyncTaskScheduler;
import net.gotzi.minestrum.command.CommandHandler;
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
import net.gotzi.minestrum.utils.ShutdownTimer;
import net.gotzi.minestrum.api.BungeeFile;

import java.io.File;

public class Minestrum extends Application {

    private Bot discordBot;
    private Bot emailBot;
    private ErrorHandler errorHandler;
    private CommandHandler commandHandler;
    private AsyncTaskScheduler<Task> taskHandler;
    private ConnectionHub connectionHub;
    private Bungee bungee;
    private File errorFolder;
    private File loggingFolder;

    public Minestrum() {
        instance = this;
    }

    @Comment.Init
    @Override
    public void start() {
        this.loadFolderStructure();

        this.connectionHub = new ConnectionHub(this);
        this.getLogger().log(LogLevel.Info, "Checking for Connection...");

        if (!this.connectionHub.checkConnection()) {
            this.earlyShutdown();
        }

        this.getLogger().log(LogLevel.Important, "Internet confirmed");

        this.getLogger().log(LogLevel.Info, "Initialize TaskHandler");

        this.taskHandler = new TaskScheduler();

        this.getLogger().log(LogLevel.Important, "TaskHandler ready!");

        this.getLogger().log(LogLevel.Info, "Starting... Discord-Bot");

        this.discordBot = new DiscordBot(
                MinestrumLogger.getDefaultGotziLogger("discord-logger", true, true),
                this.getProperties()
        ).start();

        this.getLogger().log(LogLevel.Important, "Discord-Bot ready");

        this.getLogger().log(LogLevel.Info, "Starting... Email-Bot");

        this.emailBot = new EmailBot(
                MinestrumLogger.getDefaultGotziLogger("email-logger", true, true),
                getProperties()).start();

        this.getLogger().log(LogLevel.Important, "Email-Bot ready");

        this.getLogger().log(LogLevel.Info, "Initialize ErrorHandler");

        this.errorHandler = new ErrorHandler(this);

        this.getLogger().log(LogLevel.Important, "ErrorHandler ready!");

        this.getLogger().log(LogLevel.Info, "Initialize CommandHandler");

        this.commandHandler = new CommandHandler('#');
        Task task = new Task("command-handler", this::startCommandHandler);
        this.taskHandler.runTask(task);
        this.getLogger().log(LogLevel.Important, "CommandHandler ready!");

        this.getLogger().log(LogLevel.Info, "Start BungeeCord");
        this.startBungee();

        this.getLogger().log(LogLevel.Info, "Register Commands");
        this.registerCommands();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "Shutdown Thread"));
    }

    @Override
    public void stop() {
        new Thread( "Shutdown Thread" ) {
            @Override
            public void run() {
                try {
                    getDiscordBot().stop();
                    getBungee().stop();
                    getTaskHandler().stopTasks();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.exit(0);
            }
        }.start();
    }

    @Override
    public void earlyShutdown() {
        this.getLogger().log(LogLevel.Warning, "Canceling Start...");
        this.getLogger().log(LogLevel.Warning, "Application will shut down in 30-1");
        ShutdownTimer.startDefaultShutdown(this);
    }

    private void registerCommands() {
        this.commandHandler.registerCommand(new StopCommand("stop", this));
        this.commandHandler.registerCommand(new VersionCommand("version", this));
        this.commandHandler.registerCommand(new ErrorCommand("error", this));
    }

    private void startBungee() {
        try {
            BungeeFile.setFolder(this.getProperties().getProperty("bungee_dir"));
            this.bungee = new Bungee(this.getLogger(), this.getProperties());
            this.bungee.start();
        } catch (Exception e) {
            this.errorHandler.registerError(new ErrorView("failed while starting Proxy", e));
        }
    }

    private void startCommandHandler() {
        this.getLogger().log(LogLevel.Info, "Loading CommandHandler");
        this.commandHandler.scanLoop(getConsoleReader());
    }

    private void loadFolderStructure() {
        FileUtils.initDir(this.getProperties().getProperty("bungee_dir"));

        String errorDir = this.getProperties().getProperty("error_dir");
        this.errorFolder = FileUtils.initDir(errorDir);

        String loggingDir = this.getProperties().getProperty("logging_dir");
        this.loggingFolder = FileUtils.initDir(loggingDir);
    }

    @Comment.Getter
    public Bungee getBungee() {
        return bungee;
    }

    @Comment.Getter
    public Bot getDiscordBot() {
        return discordBot;
    }

    @Comment.Getter
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Comment.Getter
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Comment.Getter
    public Bot getEmailBot() {
        return emailBot;
    }

    @Comment.Getter
    public File getErrorFolder() {
        return errorFolder;
    }

    @Comment.Getter
    public File getLoggingFolder() {
        return loggingFolder;
    }

    @Comment.Getter
    public AsyncTaskScheduler<Task> getTaskHandler() {
        return taskHandler;
    }

    @Comment.Getter
    public ConnectionHub getConnectionHub() {
        return connectionHub;
    }

    public static Minestrum getInstance() {
        return instance;
    }

    private static Minestrum instance;
}
