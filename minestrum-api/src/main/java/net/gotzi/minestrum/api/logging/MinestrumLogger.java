package net.gotzi.minestrum.api.logging;

import jline.console.ConsoleReader;
import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.format.ansi.AnsiColor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.logging.*;

public class MinestrumLogger extends java.util.logging.Logger {

    /**
     * It returns a new GLogger object with the following parameters:
     *         - Information: The information prefix
     *         - Warning: The warning prefix
     *         - Debug: The debug prefix
     *         - Colors.WHITE: The information color
     *         - Colors.CYAN: The warning color
     *
     * @return A new GLogger object with the following parameters:
     *         - Information
     *         - Warning
     *         - Debug
     *         - Colors.WHITE
     *         - Colors.CYAN
     */
    public static MinestrumLogger getNativeLogger(String name) {
        return new MinestrumLogger(name);
    }

    public static MinestrumLogger getConsoleLogger(String name, ConsoleReader reader, String prompt) {
        MinestrumLogger logger = new MinestrumLogger(name);
        TerminalHandler terminalHandler = new TerminalHandler(reader);
        terminalHandler.setFormatter(new LogDefaultFormatter(true));
        logger.addHandler(terminalHandler);
        logger.setUseParentHandlers(false);
        logger.setPrompt(prompt);

        return logger;
    }

    /*
            get Default Formatter
             */
    public static Formatter getFormat() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                return AnsiColor.removeColors(new SimpleFormatter().format(record));
            }
        };
    }

    private String name;

    private String prompt;

    private boolean debug;

    @Comment.Constructor
    public MinestrumLogger(String name) {
        super(name, null);
        LogManager.getLogManager().addLogger(this);
    }

    @Override
    public void log(java.util.logging.Level level, String msg, Object... params) {
        MessageFormat messageFormat = new MessageFormat(msg);
        if (debug) {
            if (level != LogLevel.Debug)
                super.log(level, messageFormat.format(params));
        } else
            super.log(level, messageFormat.format(params));
    }

    @Override
    public void log(java.util.logging.Level level, String msg, Object param1) {
        MessageFormat messageFormat = new MessageFormat(msg);
        if (debug) {
            if (level != LogLevel.Debug)
                super.log(level, messageFormat.format(new Object[]{param1}));
        } else
            super.log(level, messageFormat.format(new Object[]{param1}));
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * It creates a new file, adds a new handler to the logger, and writes the infoHeader to the file
     *
     * @param file The file to save the log to.
     * @param formatter The formatter to use for the log.
     * @param infoHeader An array of strings that will be written to the file before any log messages.
     */
    public void setLogSaveForFile(File file, Formatter formatter, String[] infoHeader) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        addHandler(new StreamHandler(fileOutputStream, formatter));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        for (String line : infoHeader) {
            outputStreamWriter.append(line).append("\n");
        }
        outputStreamWriter.flush();
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
