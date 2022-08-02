package at.gotzi.minestrum;

import at.gotzi.api.GHelper;
import at.gotzi.api.ano.Comment;
import at.gotzi.api.command.CommandHandler;
import at.gotzi.api.logging.GLevel;
import at.gotzi.api.logging.GLogger;
import at.gotzi.minestrum.api.Application;
import at.gotzi.minestrum.api.Bot;
import at.gotzi.minestrum.api.task.AsyncTaskHandler;
import at.gotzi.minestrum.commands.ErrorCommand;
import at.gotzi.minestrum.commands.StopCommand;
import at.gotzi.minestrum.commands.VersionCommand;
import at.gotzi.minestrum.email.EmailBot;
import at.gotzi.minestrum.error.ErrorHandler;
import at.gotzi.minestrum.discord.DiscordBot;
import at.gotzi.minestrum.api.error.ErrorView;
import at.gotzi.minestrum.task.Task;
import at.gotzi.minestrum.task.TaskHandler;
import at.gotzi.minestrum.utils.ConnectionHelper;
import at.gotzi.minestrum.utils.ShutdownTimer;
import jline.console.ConsoleReader;
import net.md_5.bungee.Bungee;
import net.md_5.bungee.api.BungeeFile;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Minestrum extends Application {

    private Bot discordBot;
    private Bot emailBot;
    private ErrorHandler errorHandler;
    private CommandHandler commandHandler;
    private AsyncTaskHandler<Task> taskHandler;
    private Bungee bungee;
    private File errorFolder;
    private File loggingFolder;

    private File file;

    public Minestrum() {
        instance = this;
    }

    @Comment.Init
    @Override
    public void start() {
        this.loadFolders();

        this.getLogger().log(GLevel.Info, "Checking for Connection...");

        if (!ConnectionHelper.ping("https://www.google.com")) {
            this.getLogger().log(GLevel.Warning, "Could not Connect to Google Servers, assume that System got no Internet");
            this.earlyShutdown();
        }

        this.getLogger().log(GLevel.Important, "Internet confirmed");

        this.getLogger().log(GLevel.Info, "Initialize TaskHandler");

        this.taskHandler = new TaskHandler();

        this.getLogger().log(GLevel.Important, "TaskHandler ready!");

        this.getLogger().log(GLevel.Info, "Starting... Discord-Bot");

        this.discordBot = new DiscordBot(
                GLogger.getDefaultGotziLogger("discord-logger", true, true),
                this.getProperties()
        ).start();

        this.getLogger().log(GLevel.Important, "Discord-Bot ready");

        this.getLogger().log(GLevel.Info, "Starting... Email-Bot");

        this.emailBot = new EmailBot(
                GLogger.getDefaultGotziLogger("email-logger", true, true),
                getProperties()).start();

        this.getLogger().log(GLevel.Important, "Email-Bot ready");

        this.getLogger().log(GLevel.Info, "Initialize ErrorHandler");

        this.errorHandler = new ErrorHandler(this);

        this.getLogger().log(GLevel.Important, "ErrorHandler ready!");

        this.getLogger().log(GLevel.Info, "Initialize CommandHandler");

        this.commandHandler = new CommandHandler('#');
        Task task = new Task("command-handler", this::startCommandHandler);
        this.taskHandler.runTask(task);

        this.getLogger().log(GLevel.Important, "CommandHandler ready!");

        try {
            this.getLogger().log(GLevel.Info, "Start BungeeCord");
            BungeeFile.setFolder(this.getProperties().getProperty("bungee_dir"));
            this.bungee = new Bungee(this.getLogger(), this.getProperties());
            this.bungee.start();
        } catch (Exception e) {
            this.errorHandler.registerError(new ErrorView("failed while starting Proxy", e));
        }

        this.getLogger().log(GLevel.Info, "Register Commands");
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
        this.getLogger().log(GLevel.Warning, "Canceling Start...");
        this.getLogger().log(GLevel.Warning, "Application will shut down in 30-1");
        ShutdownTimer.startDefaultShutdown();
    }

    private void registerCommands() {
        this.commandHandler.registerCommand(new StopCommand("stop", this));
        this.commandHandler.registerCommand(new VersionCommand("version", this));
        this.commandHandler.registerCommand(new ErrorCommand("error", this));
    }

    private void startCommandHandler() {
        this.getLogger().log(GLevel.Debug, GHelper.getCallerClassName());
        this.getLogger().log(GLevel.Info, "Loading CommandHandler");
        Scanner scanner = new Scanner(System.in);
        this.commandHandler.scanLoop(scanner::nextLine);
    }

    private void loadFolders() {
        GHelper.initDir(this.getProperties().getProperty("bungee_dir"));

        String errorDir = this.getProperties().getProperty("error_dir");
        GHelper.initDir(errorDir);
        this.errorFolder = new File(errorDir);

        String loggingDir = this.getProperties().getProperty("logging_dir");
        GHelper.initDir(loggingDir);
        this.loggingFolder = new File(loggingDir);
    }

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

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Comment.Getter
    public Bot getEmailBot() {
        return emailBot;
    }

    public File getErrorFolder() {
        return errorFolder;
    }

    public File getLoggingFolder() {
        return loggingFolder;
    }

    public AsyncTaskHandler<Task> getTaskHandler() {
        return taskHandler;
    }

    public static Minestrum getInstance() {
        return instance;
    }

    private static Minestrum instance;
}
