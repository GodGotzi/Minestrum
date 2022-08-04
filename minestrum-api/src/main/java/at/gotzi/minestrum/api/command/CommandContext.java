package at.gotzi.minestrum.api.command;

import java.util.Properties;

public record CommandContext(String cmd, String[] args, Properties properties) { }
