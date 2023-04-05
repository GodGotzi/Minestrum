/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api.command;

import java.util.Properties;

public record CommandContext(String cmd, String[] args, Properties properties) { }