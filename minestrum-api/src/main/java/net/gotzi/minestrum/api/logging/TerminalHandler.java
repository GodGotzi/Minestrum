package net.gotzi.minestrum.api.logging;

import jline.console.ConsoleReader;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TerminalHandler extends Handler {

    private final ConsoleReader reader;

    protected TerminalHandler(ConsoleReader reader) {
        this.reader = reader;
    }

    @Override
    public void publish(LogRecord record) {
        String formatted = getFormatter().format(record);

        try {
            System.out.print(formatted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
