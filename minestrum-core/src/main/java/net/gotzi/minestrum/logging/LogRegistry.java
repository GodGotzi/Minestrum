package net.gotzi.minestrum.logging;

import net.gotzi.minestrum.api.registry.Registry;
import net.gotzi.minestrum.api.registry.history.FileHistory;
import net.gotzi.minestrum.api.registry.history.History;

import java.io.File;
import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogRegistry implements Registry<LogRecord> {

    private final History<LogRecord> history;

    public LogRegistry(File file, Formatter infoFormatter, Formatter showFormatter) {
        try {
            this.history = new FileHistory<>(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        history.setInfoFormatter(infoFormatter::format);
        history.setShowFormatter(showFormatter::format);
    }

    @Override
    public void register(LogRecord logRecord) {
        //Minestrum.getInstance().getLogger().log(LogLevel.Debug, logRecord.getMessage());
        history.addRawElement(logRecord);
    }

    @Override
    public void unregister(LogRecord logRecord) {
        history.removeRawElement(logRecord);
    }
}