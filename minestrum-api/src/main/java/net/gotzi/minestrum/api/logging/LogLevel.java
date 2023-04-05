/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api.logging;

import net.gotzi.minestrum.api.format.ansi.AnsiColor;

public class LogLevel extends java.util.logging.Level {

    public static final LogLevel ERROR = new LogLevel("Error", AnsiColor.RED, 950);
    public static final LogLevel INFO = new LogLevel(AnsiColor.GREEN + "Information", AnsiColor.WHITE, 951);
    public static final LogLevel DEBUG = new LogLevel(AnsiColor.CYAN + "Debug", AnsiColor.WHITE, 952);
    public static final LogLevel WARNING = new LogLevel("Warning", AnsiColor.YELLOW, 953);
    public static final LogLevel IMPORTANT = new LogLevel("Important", AnsiColor.GREEN, 954);
    public static final LogLevel FINE = new LogLevel("Fine", AnsiColor.GREEN, 955);

    public String color;

    protected LogLevel(String name, String color, int value) {
        super(color + name + color, value);

        this.color = color;
    }
}
