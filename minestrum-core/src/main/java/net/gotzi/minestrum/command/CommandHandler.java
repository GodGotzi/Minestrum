/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.command;

import jline.console.completer.Completer;
import net.gotzi.minestrum.api.command.CommandContext;
import net.gotzi.minestrum.api.command.CommandException;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.task.AsyncTaskScheduler;
import net.gotzi.minestrum.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class CommandHandler implements Completer {

    private final Properties properties = new Properties();
    private final Map<String, Command> commandMap = new LinkedHashMap<>();
    private final char commandChar;
    private final AsyncTaskScheduler<Task> taskScheduler;
    private Logger logger;

    public CommandHandler(AsyncTaskScheduler<Task> taskScheduler, char commandChar) {
        this.commandChar = commandChar;
        this.taskScheduler = taskScheduler;

        InputStream in = CommandHandler.class.getClassLoader().getResourceAsStream("command-handler.properties");

        try {
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError();
        }
    }

    public void scanLoop(CommandScanner commandScanner) {
        String scan;

        try {
            scan = commandScanner.scan();
            if (scan != null && scan.length() != 0) {
                scan = scan.trim();

                if (isCommand(scan))
                    startCommandExecuteTask(scan);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw new CommandException(e.getMessage());
        }
    }

    private void startCommandExecuteTask(String line) {
        Task task = new Task("command-execute", () -> executeCommand(line));
        taskScheduler.runTask(task);
    }

    /**
     * It takes a GCommand object and adds it to the commandMap HashMap
     *
     * @param command The command you want to register.
     */
    public synchronized void registerCommand(Command command) {
        command.setLogger(this.logger);
        commandMap.put(command.getLabel(), command);
    }

    private boolean isCommand(String line) {
        if (line.charAt(0) == commandChar || commandChar == ' ') {
            if (commandChar != ' ') line = line.substring(1);
            String[] cmdSplit = line.split(" ", 2);

            if (!commandMap.containsKey(cmdSplit[0])) {
                this.logger.log(LogLevel.INFO, properties.getProperty("commandNotExists"), cmdSplit[0]);
                return false;
            }

            return true;
        }

        return false;
    }

    /**
     * It takes a string, splits it into a command and arguments, and then executes the command with the arguments
     *
     * @param line The line of text that was sent to the bot.
     */
    public synchronized void executeCommand(String line) {
        if (commandChar != ' ') line = line.substring(1);
        String[] cmdSplit = line.split(" ", 2);
        if (cmdSplit.length < 2) executeCommand(cmdSplit[0], new String[]{});
        else executeCommand(cmdSplit[0], cmdSplit[1].split(" "));
    }

    /**
     * It executes a command
     *
     * @param cmd The command name
     * @param args The arguments of the command.
     */
    public void executeCommand(String cmd, String[] args) {
        commandMap.get(cmd).execute(new CommandContext(cmd, args, properties));
    }

    public Properties getProperties() {
        return properties;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        final SortedSet<String> commands = new TreeSet<>(commandMap.keySet());
        String prefix;

        if (buffer == null) {
            candidates.addAll(commands);
        } else if (buffer.charAt(0) == commandChar || commandChar == ' ') {
            prefix = buffer;

            if (buffer.length() > cursor) {
                prefix = buffer.substring(0, cursor).trim();
            }

            if (commandChar != ' ') prefix = prefix.substring(1);

            String[] prefixSplit = prefix.split(" ", 2);

            if (prefixSplit.length == 1) {
                for (String match : commands.tailSet(prefix)) {

                    if (!match.startsWith(prefix)) {
                        break;
                    }

                    candidates.add(match);
                }
            } else {
                if (!commandMap.containsKey(prefixSplit[0])) {
                    candidates.clear();
                } else {
                    String[] argSplit = prefixSplit[1].split(" ");
                    String[] prefixArgs = Arrays.copyOfRange(argSplit, 0, argSplit.length-1);
                    Command command = commandMap.get(prefixSplit[0]);
                    List<CharSequence> argCandidates = command.completeArgument(prefixArgs, argSplit[argSplit.length-1]);
                    argCandidates.forEach(charSequence -> candidates.add(prefixSplit[0] + " " + charSequence));
                }
            }
        }

        if (candidates.size() == 1) {
            candidates.set(0, String.valueOf((commandChar == ' ' ? "" : commandChar)) + candidates.get(0));
        }

        return candidates.isEmpty() ? -1 : 0;
    }
}
