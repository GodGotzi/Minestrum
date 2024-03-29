/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api.logging.format;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public abstract class LogFormatter extends Formatter {

    private final boolean colors;

    public LogFormatter(boolean colors) {
        this.colors = colors;
    }

    @Override
    public String format(LogRecord record) {
        if (this.colors) return formatWithColors(record);
        return formatWithOutColors(record);
    }

    public abstract String formatWithColors(LogRecord logRecord);

    public abstract String formatWithOutColors(LogRecord logRecord);

}