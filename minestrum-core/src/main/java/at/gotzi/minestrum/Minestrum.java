package at.gotzi.minestrum;

import at.gotzi.api.GHelper;
import at.gotzi.api.ano.Comment;
import at.gotzi.api.logging.GLevel;
import at.gotzi.minestrum.email.EmailBot;
import at.gotzi.minestrum.error.ErrorHandler;
import at.gotzi.minestrum.discord.DiscordBot;
import at.gotzi.minestrum.error.ErrorView;
import at.gotzi.minestrum.utils.ConnectionHelper;
import at.gotzi.minestrum.utils.ShutdownTimer;
import net.md_5.bungee.Bungee;
import net.md_5.bungee.api.BungeeFile;

import java.util.Scanner;

public class Minestrum extends Application {

    private DiscordBot discordBot;
    private EmailBot emailBot;
    private ErrorHandler errorHandler;
    private Bungee bungee;

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
            this.getLogger().log(GLevel.Warning, "Canceling Start...");
            this.getLogger().log(GLevel.Warning, "Application will shut down in 30-1");

            ShutdownTimer.startDefaultShutdown();
        }

        this.getLogger().log(GLevel.Important, "Internet confirmed");


        this.getLogger().log(GLevel.Info, "Start BungeeCord");

        this.getLogger().log(GLevel.Info, "Starting... Discord-Bot");
        this.discordBot = new DiscordBot().start();
        this.getLogger().log(GLevel.Important, "Discord-Bot ready");
        this.getLogger().log(GLevel.Info, "Starting... Email-Bot");
        this.emailBot = new EmailBot().start();
        this.getLogger().log(GLevel.Important, "Email-Bot ready");

        this.getLogger().log(GLevel.Info, "Initialize ErrorHandler");
        this.errorHandler = new ErrorHandler(this);
        this.getLogger().log(GLevel.Important, "ErrorHandler ready!");

        try {
            BungeeFile.setFolder(this.getProperties().getProperty("bungee_dir"));
            this.bungee = new Bungee(this.getLogger(), this.getProperties());
            this.bungee.start();
        } catch (Exception e) {
            this.errorHandler.registerError(new ErrorView("failed while starting Proxy", e));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        new Scanner(System.in).nextLine();
    }

    public void stop() {
        getBungee().getReconnectHandler().close();
        getDiscordBot().getJda().shutdownNow();
        getBungee().stop();
    }

    public Bungee getBungee() {
        return bungee;
    }

    private void loadFolders() {
        GHelper.initDir(this.getProperties().getProperty("bungee_dir"));
    }

    @Comment.Getter
    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    @Comment.Getter
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Comment.Getter
    public EmailBot getEmailBot() {
        return emailBot;
    }

    public static Minestrum getInstance() {
        return instance;
    }

    private static Minestrum instance;
}
