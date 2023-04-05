/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.communicate.receiver;

public class PacketArgumentValue extends PacketArgument {

    public PacketArgumentValue(int index, PacketArgument[] subArguments, PacketAction packetAction) {
        super("", index, subArguments, packetAction);
    }

    public PacketArgumentValue(int index, PacketAction packetAction) {
        super("", index, packetAction);
    }
}
