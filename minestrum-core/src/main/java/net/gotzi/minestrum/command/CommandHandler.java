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

    private Task task;

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
            throw new CommandException(e.getMessage());
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
                this.logger.log(LogLevel.Info, properties.getProperty("commandNotExists"), cmdSplit[0]);
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

        if (buffer == null) {
            candidates.addAll(commands);
        } else {
            String prefix = buffer;
            if (buffer.length() > cursor) {
                prefix = buffer.substring(0, cursor);
            }
            for (String match : commands.tailSet(prefix)) {
                if (!match.startsWith(prefix)) {
                    break;
                }
                candidates.add(match);
            }
        }
        if (candidates.size() == 1) {
            candidates.set(0, candidates.get(0) + " ");
        }
        return candidates.isEmpty() ? -1 : 0;

        /*
        if (buffer != null && buffer.length() != 0) {

            if (isCommand(buffer, false)) {
                String[] cmdSplit = buffer.split(" ", 2);
                return completeCommand(cmdSplit[0], cmdSplit[1], cursor, candidates);
            } else if ((buffer.charAt(0) == commandChar || commandChar == ' ') && buffer.length() == cursor) {
                this.logger.log(LogLevel.Debug, "2");

                commandMap.values().forEach(command ->
                        candidates.add((commandChar == ' ' ? "" : commandChar) + command.getLabel())
                );

                for (int i = 0; i < buffer.length(); i++) {
                    for (CharSequence str : new ArrayList<>(candidates)) {
                        if (i == str.length() || buffer.charAt(i) != str.charAt(i))
                            candidates.remove(str);
                    }
                }

                if (buffer.charAt(0) == commandChar) return -1;
            }

            this.logger.log(LogLevel.Debug, "end");
        }

        return 0;*/
    }

    private int completeCommand(String cmd, String buffer, int cursor, List<CharSequence> candidates) {


        return 0;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
