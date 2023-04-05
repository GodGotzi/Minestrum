/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.communicate.receiver;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class PacketArgument implements Comparable<PacketArgument> {
    private SortedSet<PacketArgument> subArguments;
    private final String label;
    private final int index;
    private PacketAction packetAction;

    public PacketArgument(String label, int index, PacketArgument[] subArguments, PacketAction packetAction) {
        this.label = label;
        this.index = index;
        this.subArguments = new TreeSet<>(List.of(subArguments));
        this.packetAction = packetAction;
    }

    public PacketArgument(String label, int index, PacketAction packetAction) {
        this.label = label;
        this.index = index;
        this.packetAction = packetAction;
    }

    public void setSubArguments(SortedSet<PacketArgument> subArguments) {
        this.subArguments = subArguments;
    }

    public void setPacketAction(PacketAction packetAction) {
        this.packetAction = packetAction;
    }

    public String getLabel() {
        return label;
    }

    public int getIndex() {
        return index;
    }

    public SortedSet<PacketArgument> getSubArguments() {
        return subArguments;
    }

    public PacketAction getPacketAction() {
        return packetAction;
    }

    @Override
    public int compareTo(PacketArgument o) {
        return getLabel().compareTo(o.getLabel());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PacketArgument arg)
            return getLabel().equals(arg.getLabel());

        return false;
    }

    @Override
    public String toString() {
        return getLabel().equals("") ? "!val" : getLabel();
    }
}