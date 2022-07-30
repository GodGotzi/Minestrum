package at.gotzi.minestrum.discord;

import at.gotzi.api.logging.GDefaultFormatter;
import at.gotzi.api.logging.GLevel;
import at.gotzi.api.logging.GLogger;
import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.api.Bot;
import at.gotzi.minestrum.utils.PropertyHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;

public class DiscordBot extends Bot<DiscordBot> {

    private final GLogger logger;

    private JDA jda;

    public DiscordBot() {
        this.logger = GLogger.getDefaultGotziLogger("discord-logger", true, true);
    }

    @Override
    public DiscordBot start() {

        String token = PropertyHelper.clearHidingProperty(
                Minestrum.getInstance().getProperty("dc_token").toString()
        );

        try {
            this.logger.log(GLevel.Info, "Discord-Bot logging in...");

            final JDABuilder builder= JDABuilder.createDefault(token);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setActivity(Activity.watching("over Minestrum Server"));

            this.jda = builder.build();

            final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            this.jda.addEventListener(new ReadyListener(atomicBoolean));

            while (!atomicBoolean.get()) {
                this.logger.log(GLevel.Info, "Waiting for Discord Bot...");
                Thread.sleep(200);
            }

            this.logger.log(GLevel.Info, "Discord-Bot is ready to hear your commands!");

        } catch (Exception e) {
            Minestrum.getInstance().getLogger().log(GLevel.Warning, "Could not build Discord Bot -> " + e.getMessage());
            return null;
        }


        final long errorChannelID = Long.parseLong(Minestrum.getInstance().getProperty("dc_error_log_channel").toString());
        final Handler errorhandler = new DiscordErrorHandler("Error", this.jda.getTextChannelById(errorChannelID));
        errorhandler.setFormatter(new GDefaultFormatter(false));
        setErrorhandler(errorhandler);

        return this;
    }

    public JDA getJda() {
        return jda;
    }
}