/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.discord;

import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.utils.PropertyUtils;
import net.gotzi.minestrum.api.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class DiscordBot extends Bot {

    private final Logger logger;
    private final Properties properties;
    private final Formatter formatter;

    private JDA jda;

    public DiscordBot(Logger logger, Formatter formatter, Properties properties) {
        this.logger = logger;
        this.properties = properties;
        this.formatter = formatter;
    }

    @Override
    public DiscordBot start() {

        String token = PropertyUtils.clearHidingProperty(
                this.properties.getProperty("dc_token")
        );

        try {
            this.logger.log(LogLevel.INFO, "Discord-Bot logging in...");

            final JDABuilder builder= JDABuilder.createDefault(token);
            builder.setStatus(OnlineStatus.ONLINE);

            builder.setActivity(Activity.playing("Minestrum"));

            this.jda = builder.build();

            final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            this.jda.addEventListener(new ReadyListener(atomicBoolean));

            while (!atomicBoolean.get()) {
                this.logger.log(LogLevel.INFO, "Waiting for Discord Bot...");
                Thread.sleep(200);
            }

            this.logger.log(LogLevel.INFO, "Discord-Bot is ready to hear your commands!");

        } catch (Exception e) {
            this.logger.log(LogLevel.WARNING, "Could not build Discord Bot -> " + e.getMessage());
            return null;
        }

        final long errorChannelID = Long.parseLong(this.properties.getProperty("dc_error_log_channel"));
        final Handler errorhandler = new ErrorLoggingHandler("Error", this.jda.getTextChannelById(errorChannelID));
        errorhandler.setFormatter(formatter);
        setErrorhandler(errorhandler);

        return this;
    }

    @Override
    public void stop() {
        try {
            this.jda.shutdown();
        } catch (Exception e) {
            this.jda.shutdownNow();
        }
    }
}