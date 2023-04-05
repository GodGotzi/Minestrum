/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api.format;

public class LineBuilder {

    private final StringBuilder stringBuilder = new StringBuilder();

    public void addLine(String str) {
        stringBuilder.append(str).append("\n");
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
