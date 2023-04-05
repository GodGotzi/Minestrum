/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.communicate.receiver;

import java.util.*;


public class Packet {

    private final SortedSet<PacketArgument> arguments = new TreeSet<>();
    private final String label;
    private PacketAction nativeAction;

    public Packet(String label) {
        this.label = label;
    }

    public Packet(String label, PacketAction nativeAction) {
        this.label = label;
        this.nativeAction = nativeAction;
    }

    /**
     * If the command has no arguments, run the nullAction. If the command has arguments, check if the first argument is
     * valid. If it is, check if the next argument is valid. If it is, check if the next argument is valid. If it is, run
     * the command action
     *
     * @param packetContext The context of the command.
     */
    public void execute(PacketContext packetContext) {
        if (packetContext.args().length == 0 && nativeAction != null) {
            nativeAction.run(packetContext);
            return;
        }

        PacketArgument packetArgument =
                arguments.stream().filter(arg -> filterArguments(arg, packetContext))
                        .findFirst().orElse(null);

        if (packetArgument == null) {
            return;
        } else if (packetContext.args().length == 1) {
            packetArgument.getPacketAction().run(packetContext);
        }

        for (int i = 0; i < packetContext.args().length-1; i++) {
            packetArgument = getNextArgument(packetArgument, packetContext);
            if (packetArgument == null) {
                return;
            }

            if (packetArgument.getIndex() == (packetContext.args().length-1) && packetArgument.getPacketAction() != null) {
                packetArgument.getPacketAction().run(packetContext);
            }
        }
    }

    /**
     * It returns the next argument in the command chain
     *
     * @param argument The current argument that is being processed.
     * @param packetContext The context of the command.
     * @return The next argument in the command.
     */
    private PacketArgument getNextArgument(PacketArgument argument, PacketContext packetContext) {
        if (argument.getSubArguments() == null) return null;
        return argument.getSubArguments().stream().filter(arg ->
                filterArguments(arg, packetContext)
        ).findFirst().orElse(null);
    }

    /**
     * "If the argument's index is less than the number of arguments in the command context, and the argument's label is
     * equal to the argument at the index in the command context, or the argument is a value argument, return true."
     *
     * The first part of the function checks if the argument's index is less than the number of arguments in the command
     * context. This is to make sure that the argument is not out of bounds
     *
     * @param argument The argument that is being checked.
     * @param packetContext The context of the command.
     * @return A boolean value.
     */
    private boolean filterArguments(PacketArgument argument, PacketContext packetContext) {
        return argument.getIndex() < packetContext.args().length &&
                (argument.getLabel()
                        .equals(packetContext.args()[argument.getIndex()]) || argument instanceof PacketArgumentValue);
    }

    /**
     * If the argument doesn't already exist, add it to the list
     *
     * @param gArgument The argument to add to the list of arguments.
     */
    public void addArgument(PacketArgument gArgument) {
        if (arguments.stream().noneMatch(argument -> argument.getLabel().equals(gArgument.getLabel())))
            arguments.add(gArgument);
    }

    public String getLabel() {
        return label;
    }

    public SortedSet<PacketArgument> getArguments() {
        return Collections.unmodifiableSortedSet(arguments);
    }

    public void setNativeAction(PacketAction nativeAction) {
        this.nativeAction = nativeAction;
    }

}