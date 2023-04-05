/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.communicate.receiver;

import java.util.*;

public class PacketReceiver {

    private final Map<String, Packet> commandMap = new LinkedHashMap<>();

    /**
     * It takes a GCommand object and adds it to the commandMap HashMap
     *
     * @param packet The command you want to register.
     */
    public synchronized void registerCommand(Packet packet) {
        commandMap.put(packet.getLabel(), packet);
    }

    /**
     * It takes a string, splits it into a command and arguments, and then executes the command with the arguments
     *
     * @param line The line of text that was sent to the bot.
     */
    public synchronized void executeCommand(String line, String channel) {
        String[] cmdSplit = line.split(" ", 2);
        if (cmdSplit.length < 2) executeCommand(cmdSplit[0], new String[]{}, channel);
        else executeCommand(cmdSplit[0], cmdSplit[1].split(" "), channel);
    }

    /**
     * It executes a command
     *
     * @param cmd The command name
     * @param args The arguments of the command.
     */
    public void executeCommand(String cmd, String[] args, String channel) {
        commandMap.get(cmd).execute(new PacketContext(cmd, args, channel));
    }

}
