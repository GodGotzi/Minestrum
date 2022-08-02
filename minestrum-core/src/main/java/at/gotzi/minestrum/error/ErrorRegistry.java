package at.gotzi.minestrum.error;

import at.gotzi.minestrum.api.registry.History;
import at.gotzi.minestrum.api.registry.Registry;
import at.gotzi.minestrum.api.error.ErrorView;

import java.io.File;
import java.io.IOException;

public final class ErrorRegistry implements Registry<ErrorView> {

    private final History<ErrorView> history;

    public ErrorRegistry(File file) {
        try {
            this.history = new FileHistory(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ErrorView errorView) {
        history.addElement(errorView);
    }

    @Override
    public void unregister(ErrorView errorView) {
        history.removeElement(errorView);
    }

    public History<ErrorView> getHistory() {
        return history;
    }
}