package at.gotzi.minestrum.api.logging;

import at.gotzi.minestrum.api.format.ansi.AnsiColor;

public class LogLevel extends java.util.logging.Level {

    public static final LogLevel Error = new LogLevel("Error", AnsiColor.RED, 950);
    public static final LogLevel Info = new LogLevel(AnsiColor.GREEN + "Information", AnsiColor.WHITE, 951);
    public static final LogLevel Debug = new LogLevel(AnsiColor.CYAN + "Debug", AnsiColor.WHITE, 952);
    public static final LogLevel Warning = new LogLevel("Warning", AnsiColor.YELLOW, 953);
    public static final LogLevel Important = new LogLevel("Important", AnsiColor.GREEN, 954);

    public String color;

    protected LogLevel(String name, String color, int value) {
        super(color + name + color, value);

        this.color = color;
    }
}
