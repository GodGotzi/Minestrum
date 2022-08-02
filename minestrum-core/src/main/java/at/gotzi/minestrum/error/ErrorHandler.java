package at.gotzi.minestrum.error;

import at.gotzi.api.GHelper;
import at.gotzi.api.ano.Comment;
import at.gotzi.api.logging.GLevel;
import at.gotzi.api.logging.GLogger;
import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.api.error.ErrorView;
import at.gotzi.minestrum.api.registry.HistoryView;
import at.gotzi.minestrum.utils.TimeHelper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class ErrorHandler {

    private final Minestrum minestrum;
    private final Logger errorLogger;

    private final ErrorRegistry registry;

    @Comment.Constructor
    public ErrorHandler(Minestrum minestrum) {
        this.minestrum = minestrum;

        File file = new File(this.minestrum.getErrorFolder().getPath() + "/" +
                TimeHelper.getSimpleDateForFile() + ".log");
        GHelper.initFile(file);

        this.registry = new ErrorRegistry(file);

        this.errorLogger = GLogger.getDefaultGotziLogger("error-logger", true, true);
        try {
            loadHandlers();
        } catch (IOException e) {
            minestrum.getLogger().warning("Could not start ErrorHandler -> " + e.getMessage());
        }
    }

    @Comment.Init
    private void loadHandlers() throws IOException {
        final Handler dcErrorHandler = minestrum.getDiscordBot().getErrorhandler();
        final Handler emailErrorHandler = minestrum.getEmailBot().getErrorhandler();

        ((GLogger)errorLogger).setLogSaveForFile(new File("test.txt"), GLogger.getFormat(), new String[]{});

        if (!Minestrum.DEBUG) {
            errorLogger.addHandler(dcErrorHandler);
            errorLogger.addHandler(emailErrorHandler);
        }
    }

    public void registerError(ErrorView view) {
        errorLogger.log(GLevel.Error, view.msg() + " -> " + view.exception().getMessage() + "\n" + view.stackTrace());
        this.registry.register(view);
    }

    public ErrorRegistry getRegistry() {
        return this.registry;
    }

    public Collection<HistoryView<ErrorView>> getHistory() {
        return Collections.unmodifiableCollection(this.registry.getHistory());
    }
}
