package at.gotzi.minestrum.api.logging;

import at.gotzi.minestrum.api.format.ansi.AnsiColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;

public class LogDefaultFormatter extends LogFormatter {

    public LogDefaultFormatter(boolean colors) {
        super(colors);
    }

    @Override
    public String formatWithColors(LogRecord record) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        String threadName = Thread.currentThread().getName();
        String loggerName = record.getLoggerName();
        String message = record.getMessage();

        if (record.getLevel() instanceof LogLevel logLevel) {
            return AnsiColor.BLUE_BRIGHT + "[" + AnsiColor.RED + date + AnsiColor.BLUE_BRIGHT + "] " + "[Thread -> " + AnsiColor.RED + threadName + AnsiColor.BLUE_BRIGHT + "] " + "[Logger -> " + AnsiColor.RED + loggerName + AnsiColor.BLUE_BRIGHT + "] " + "[" + logLevel.color + logLevel.getName() + AnsiColor.BLUE_BRIGHT + "] -> " + logLevel.color + message + AnsiColor.RESET + "\n";
        }

        return AnsiColor.BLUE_BRIGHT + "[" + AnsiColor.RED + date + AnsiColor.BLUE_BRIGHT + "] " + "[Thread -> " + AnsiColor.RED + threadName + AnsiColor.BLUE_BRIGHT + "] " + "[Logger -> " + AnsiColor.RED + loggerName + AnsiColor.BLUE_BRIGHT + "] " + "[" + record.getLevel().getName() + AnsiColor.BLUE_BRIGHT + "] -> " + message + AnsiColor.RESET + "\n";
    }

    @Override
    public String formatWithOutColors(LogRecord record) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        String threadName = Thread.currentThread().getName();
        String levelName = record.getLevel().getName();
        String loggerName = record.getLoggerName();
        String message = record.getMessage();

        return AnsiColor.removeColors("[" + date + "] " + "[Class -> " + threadName + "] " + "[Logger -> " + loggerName + "] [" + levelName + "] -> " + message + "\n");
    }
}