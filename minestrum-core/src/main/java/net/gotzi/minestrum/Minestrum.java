/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum;

import jline.console.ConsoleReader;
import lombok.Getter;
import net.gotzi.bungee.Bungee;
import net.gotzi.bungee.api.plugin.Plugin;
import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.Bot;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.task.AsyncTaskScheduler;
import net.gotzi.minestrum.command.Command;
import net.gotzi.minestrum.command.CommandArgument;
import net.gotzi.minestrum.command.CommandHandler;
import net.gotzi.minestrum.command.CommandScanner;
import net.gotzi.minestrum.command.commands.*;
import net.gotzi.minestrum.communicate.receiver.PacketReceiver;
import net.gotzi.minestrum.communicate.sender.PacketSender;
import net.gotzi.minestrum.connection.packets.BootedPacket;
import net.gotzi.minestrum.discord.DiscordBot;
import net.gotzi.minestrum.email.EmailBot;
import net.gotzi.minestrum.error.ErrorHandler;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.api.logging.format.LogDefaultFormatter;
import net.gotzi.minestrum.listener.PlayerConnectListener;
import net.gotzi.minestrum.listener.ServerListener;
import net.gotzi.minestrum.logging.MinestrumLogger;
import net.gotzi.minestrum.connection.ConnectionHub;
import net.gotzi.minestrum.server.ServerHandler;
import net.gotzi.minestrum.task.Task;
import net.gotzi.minestrum.task.TaskScheduler;
import net.gotzi.minestrum.api.logging.LogHandler;
import net.gotzi.minestrum.utils.FileUtils;
import net.gotzi.minestrum.utils.MinestrumUtils;
import net.gotzi.bungee.api.BungeeFile;
import net.gotzi.minestrum.utils.ShutdownTimer;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public class Minestrum extends Plugin {
    public static boolean DEBUG;

    @Getter
    private final Logger logger;
    @Getter
    private final ConsoleReader consoleReader;
    @Getter
    private final Properties properties;
    @Getter
    private final AsyncTaskScheduler<Task> taskHandler;
    @Getter
    private final CommandHandler commandHandler;
    @Getter
    private final LogHandler logHandler;

    @Getter
    private PacketReceiver packetReceiver;

    @Getter
    private PacketSender packetSender;

    @Getter
    private Bot discordBot;
    @Getter
    private Bot emailBot;
    @Getter
    private ErrorHandler errorHandler;
    @Getter
    private ServerHandler serverHandler;
    @Getter
    private Bungee bungee;
    @Getter
    private File errorFolder;
    @Getter
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
        this.consoleReader.addCompleter(this.commandHandler);
        this.consoleReader.setPrompt(properties.getProperty("console_prompt") + " ");

        Task task = new Task("command-handler", this::startCommandHandler);
        this.taskHandler.runRepeatingTask(task);

        this.logHandler = new LogHandler(
                consoleReader,
                new LogDefaultFormatter(true),
                new LogDefaultFormatter(false),
                loggingFolder
        );

        this.logger = MinestrumLogger.getConsoleLogger("main", this.logHandler);
        MinestrumUtils.LOGGER = this.logger;

        Minestrum.DEBUG =  Boolean.parseBoolean(this.properties.getProperty("debug"));
    }

    public void start(String[] strings) {
        this.logger.log(LogLevel.INFO, "Starting... Minestrum");

        this.logger.log(LogLevel.INFO, "Loading... Java Arguments");

        this.logger.log(LogLevel.INFO, "Logging Config");
        this.logProperties();
        this.startup();

    }

    @Comment.Init
    public void startup() {
        this.commandHandler.setLogger(
                MinestrumLogger.getConsoleLogger("command-logger",  this.logHandler)
        );

        ConnectionHub hub= new ConnectionHub(this);
        this.logger.log(LogLevel.INFO, "Checking for Connection...");

        if (!hub.checkConnection()) {
            this.earlyShutdown();
        }

        this.logger.log(LogLevel.IMPORTANT, "Internet confirmed");

        this.logger.log(LogLevel.INFO, "Starting Server Handler");
        try {
            this.serverHandler = new ServerHandler(properties,
                    MinestrumLogger.getConsoleLogger("server-hub-logger", this.logHandler), this
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.logger.log(LogLevel.INFO, "Ready!");


        this.logger.log(LogLevel.INFO, "Starting... Discord-Bot");

        LogDefaultFormatter botFormatter = new LogDefaultFormatter(false);

        this.discordBot = new DiscordBot(
                MinestrumLogger.getConsoleLogger("discord-logger", this.logHandler),
                botFormatter,
                this.properties
        ).start();

        this.logger.log(LogLevel.IMPORTANT, "Discord-Bot ready");

        this.logger.log(LogLevel.INFO, "Starting... Email-Bot");

        this.emailBot = new EmailBot(
                MinestrumLogger.getConsoleLogger("email-logger", this.logHandler),
                botFormatter,
                properties
        ).start();

        this.logger.log(LogLevel.IMPORTANT, "Email-Bot ready");

        this.logger.log(LogLevel.INFO, "Initialize ErrorHandler");

        this.errorHandler = new ErrorHandler(this,
                MinestrumLogger.getConsoleLogger("error-logger",  this.logHandler)
        );

        this.logger.log(LogLevel.IMPORTANT, "ErrorHandler ready!");

        this.logger.log(LogLevel.INFO, "Start BungeeCord");
        this.startBungee();

        this.logger.log(LogLevel.INFO, "Register Commands");
        this.registerCommands();
        this.registerPackets();

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
        this.logger.log(LogLevel.WARNING, "Canceling Start...");
        this.logger.log(LogLevel.WARNING, "Application will shut down in 30-1");
        ShutdownTimer.startDefaultShutdown(this);
    }

    private void registerPackets() {
        this.packetReceiver.registerCommand(new BootedPacket(this.serverHandler));
    }

    private void registerCommands() {
        this.commandHandler.registerCommand(new StopCommand("stop", this));
        this.commandHandler.registerCommand(new VersionCommand("version", this));
        this.commandHandler.registerCommand(new ErrorCommand("error", this));
        this.commandHandler.registerCommand(new LogCommand("log", this));
        this.commandHandler.registerCommand(new ServerCommand("server", this));

        Command command = new Command("test", commandContext ->
                this.logger.log(LogLevel.INFO, "Test command wurde ausgeführt"));
        command.addArgument(
                new CommandArgument("arg1", 0, commandContext ->
                        this.logger.log(LogLevel.INFO, "CommandArgument arg1 command wurde ausgeführt"))

        );

        this.commandHandler.registerCommand(command);
    }

    private void startBungee() {
        try {
            BungeeFile.setFolder(this.properties.getProperty("bungee_dir"));
            this.bungee = new Bungee(this.logger, this.properties);
            this.bungee.start();
            this.packetReceiver = new PacketReceiver();
            this.packetSender = new PacketSender((channel, data) ->
                    bungee.registerChannel("proxy"), this.packetReceiver);

            ServerListener serverListener = new ServerListener(this.packetReceiver);
            this.bungee.getPluginManager().registerListener(this, serverListener);
            this.bungee.getPluginManager().registerListener(this, new PlayerConnectListener(this));

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
                this.logger.log(LogLevel.INFO, prop.toString());
            else {
                String str = prop.getKey() + "=" +
                        "*".repeat(prop.getValue().toString().length());
                this.logger.log(LogLevel.INFO, str);
            }
        });
    }

    public synchronized void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public synchronized boolean isRunning() {
        return !this.shutdown;
    }
    @Getter
    private static Minestrum instance;
}
