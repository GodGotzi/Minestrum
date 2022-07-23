package at.gotzi.minestrum;

import at.gotzi.api.GHelper;
import at.gotzi.api.ano.Comment;
import at.gotzi.minestrum.email.EmailBot;
import at.gotzi.minestrum.error.ErrorHandler;
import at.gotzi.minestrum.discord.DiscordBot;
import at.gotzi.minestrum.error.ErrorView;
import at.gotzi.minestrum.utils.ConnectionHelper;
import at.gotzi.minestrum.utils.ShutdownTimer;
import at.gotzi.minestrumbungee.MinestrumBungee;
import at.gotzi.minestrumbungee.MinestrumBungee;

public class Minestrum extends Application {

    private DiscordBot discordBot;
    private EmailBot emailBot;
    private ErrorHandler errorHandler;

    private MinestrumBungee bungee;

    public Minestrum() {
        instance = this;
    }

    @Comment.Init
    @Override
    public void start() {
        this.loadFolders();

        this.getLogger().info("Checking for Connection...");

        if (!ConnectionHelper.ping("https://www.google.com")) {
            this.getLogger().warning("Could not Connect to Google Servers, assume that System got no Internet");
            this.getLogger().warning("Canceling Start...");
            this.getLogger().warning("Application will shut down in 30-1");

            ShutdownTimer.startDefaultShutdown();
        }

        this.getLogger().important("Internet confirmed");


        this.getLogger().info("Start BungeeCord");

        this.getLogger().info("Starting... Discord-Bot");
        //this.discordBot = new DiscordBot().start();
        this.getLogger().important("Discord-Bot ready");
        this.getLogger().info("Starting... Email-Bot");
        this.emailBot = new EmailBot().start();
        this.getLogger().important("Email-Bot ready");

        this.getLogger().info("Initialize ErrorHandler");
        this.errorHandler = new ErrorHandler(this);
        this.getLogger().important("ErrorHandler ready!");

        try {
            this.bungee = new MinestrumBungee(this.getLogger(), this.properties);
            this.bungee.start();
        } catch (Exception e) {
            this.errorHandler.registerError(new ErrorView("failed while starting Proxy", e));
        }
    }

    private void loadFolders() {
        GHelper.initDir(this.properties.getProperty("bungee_dir"));
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
