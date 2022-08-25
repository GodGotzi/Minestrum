package net.gotzi.minestrum.api.server.logging;

import net.gotzi.minestrum.api.registry.history.History;

import java.util.Map;

public interface ServerLogManagerInterface {

    Map<Long, History<String>> getHistories();


}
