package net.gotzi.minestrum.command;

import jline.console.ConsoleReader;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.api.command.CommandException;
import net.gotzi.minestrum.api.logging.LogLevel;
import jline.console.completer.Completer;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CommandHandler implements Completer {

    private final Properties properties = new Properties();
    private final Map<String, Command> commandMap = new LinkedHashMap<>();

    private char commandChar;

    public CommandHandler(char commandChar) {
        this.commandChar = commandChar;

        InputStream in = CommandHandler.class.getClassLoader().getResourceAsStream("command-handler.properties");

        try {
            synchronized (properties) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError();
        }
    }

    /**
     * This function will run the executeCommand function every tick, and will pass the scanner's scan() function as the
     * command, and an empty object array as the arguments.
     *
     * @param reader The reader to use.
     */
    public void scanLoop(ConsoleReader reader) {
        String scan;

        try {
            while(true) {
                scan = reader.readLine();
                if (scan != null && scan.length() != 0)
                    this.executeCommand(scan);
            }
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }
    }

    /**
     * It takes a GCommand object and adds it to the commandMap HashMap
     *
     * @param command The command you want to register.
     */
    public synchronized void registerCommand(Command command) {
        commandMap.put(command.getLabel(), command);
    }

    /**
     * It takes a string, splits it into a command and arguments, and then executes the command with the arguments
     *
     * @param line The line of text that was sent to the bot.
     */
    public synchronized void executeCommand(String line) {
        if (line.charAt(0) == commandChar || commandChar == ' ') {
            if (commandChar != ' ') line = line.substring(1);
            String[] cmdSplit = line.split(" ", 2);
            if (cmdSplit.length < 2) executeCommand(cmdSplit[0], new String[]{});
            else executeCommand(cmdSplit[0], cmdSplit[1].split(" "));
        }
    }

    /**
     * It executes a command
     *
     * @param cmd The command name
     * @param args The arguments of the command.
     */
    public void executeCommand(String cmd, String[] args) {
        if (commandMap.get(cmd) == null) {
            Command.getCommandLogger().log(LogLevel.Info, properties.getProperty("commandNotExists"), cmd);
            return;
        }

        commandMap.get(cmd).execute(new CommandContext(cmd, args, properties));
    }

    @Override
    public int complete(String s, int i, List<CharSequence> list) {

        return 0;
    }

    public synchronized void setCommandChar(char commandChar) {
        this.commandChar = commandChar;
    }

    public Properties getProperties() {
        return properties;
    }
}
