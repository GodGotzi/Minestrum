/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ErrorLoggingHandler extends Handler {

    private final TextChannel textChannel;
    private final String title;

    public ErrorLoggingHandler(String title, TextChannel textChannel) {
        this.textChannel = textChannel;
        this.title = title;
    }

    @Override
    public void publish(LogRecord record) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);

        final String formattedString = getFormatter().format(record);
        embedBuilder.setDescription(formattedString);
        this.textChannel.sendMessageEmbeds(embedBuilder.build()).complete();
    }

    @SuppressWarnings("empty")
    @Override
    public void flush() {}

    @SuppressWarnings("empty")
    @Override
    public void close() {}
}
