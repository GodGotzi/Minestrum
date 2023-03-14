/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.logging;

import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.format.ansi.AnsiColor;
import net.gotzi.minestrum.api.logging.LogDispatcher;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.logging.LoggerInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.logging.*;

public class MinestrumLogger extends java.util.logging.Logger implements LoggerInterface {

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

    public static MinestrumLogger getConsoleLogger(String name, Handler handler) {
        MinestrumLogger logger = new MinestrumLogger(name);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
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

    private final LogDispatcher<MinestrumLogger> dispatcher;
    private boolean debug;

    @Comment.Constructor
    public MinestrumLogger(String name) {
        super(name, null);
        LogManager.getLogManager().addLogger(this);
        this.dispatcher = new LogDispatcher<>(this);

        dispatcher.start();
    }

    @Override
    public void log(java.util.logging.Level level, String msg, Object... params) {
        MessageFormat messageFormat = new MessageFormat(msg);
        if (debug) {
            if (level != LogLevel.DEBUG)
                super.log(level, messageFormat.format(params));
        } else
            super.log(level, messageFormat.format(params));
    }

    @Override
    public void log(java.util.logging.Level level, String msg, Object param1) {
        MessageFormat messageFormat = new MessageFormat(msg);
        if (debug) {
            if (level != LogLevel.DEBUG)
                super.log(level, messageFormat.format(new Object[]{param1}));
        } else
            super.log(level, messageFormat.format(new Object[]{param1}));
    }

    @Override
    public void log(LogRecord record) {
        dispatcher.queue(record);
    }

    @Override
    public void doLog(LogRecord record) {
        super.log( record );
    }


    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }
}
