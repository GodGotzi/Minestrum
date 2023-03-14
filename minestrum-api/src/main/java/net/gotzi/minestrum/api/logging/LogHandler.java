/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api.logging;

import jline.console.ConsoleReader;
import net.gotzi.minestrum.utils.FileUtils;
import net.gotzi.minestrum.utils.TimeUtils;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

    private final ConsoleReader reader;
    private final LogRegistry logRegistry;

    public LogHandler(ConsoleReader reader, Formatter formatter, Formatter fileFormatter, File loggingFolder) {
        this.reader = reader;

        File file = new File(loggingFolder.getPath() + "/" +
                TimeUtils.getSimpleDateForFile() + ".log");
        FileUtils.initFile(file);

        this.logRegistry = new LogRegistry(file, formatter, fileFormatter);

        setFormatter(formatter);
    }

    @Override
    public void publish(LogRecord record) {
        String formatted = getFormatter().format(record);

        try {
            reader.print( Ansi.ansi().eraseLine( Ansi.Erase.ALL ).toString() + ConsoleReader.RESET_LINE + formatted + Ansi.ansi().reset().toString() );
            reader.drawLine();
            reader.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        logRegistry.register(record);
    }

    public LogRegistry getRegistry() {
        return logRegistry;
    }

    @SuppressWarnings("empty")
    @Override
    public void flush() {
    }

    @SuppressWarnings("empty")
    @Override
    public void close() throws SecurityException {
    }
}
