/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.error;

import net.gotzi.minestrum.api.registry.history.History;
import net.gotzi.minestrum.api.registry.Registry;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.api.registry.history.FileHistory;

import java.io.File;
import java.io.IOException;

public final class ErrorRegistry implements Registry<ErrorView> {

    private final History<ErrorView> history;

    public ErrorRegistry(File file) {
        try {
            this.history = new FileHistory<>(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ErrorView errorView) {
        history.addRawElement(errorView);
    }

    @Deprecated
    @Override
    public void unregister(ErrorView errorView) {
        history.removeElement(errorView);
    }

    public History<ErrorView> getHistory() {
        return history;
    }
}