package at.gotzi.minestrum.error;

import at.gotzi.api.ano.Comment;
import at.gotzi.api.logging.GLevel;
import at.gotzi.api.logging.GLogger;
import at.gotzi.minestrum.Minestrum;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class ErrorHandler {

    private final Minestrum minestrum;

    private final Logger errorLogger;

    @Comment.Constructor
    public ErrorHandler(Minestrum minestrum) {
        this.minestrum = minestrum;
        this.errorLogger = GLogger.getDefaultGotziLogger("error-logger", true, true);

        try {
            loadHandlers();
        } catch (IOException e) {
            minestrum.getLogger().warning("Could not start ErrorHandler -> " + e.getMessage());
        }
    }

    @Comment.Init
    private void loadHandlers() throws IOException {
        //final Handler dcErrorHandler = minestrum.getDiscordBot().getErrorhandler();
        final Handler emailErrorHandler = minestrum.getEmailBot().getErrorhandler();

        ((GLogger)errorLogger).setLogSaveForFile(new File("test.txt"), GLogger.getFormat(), new String[]{});

        if (!Minestrum.DEBUG) {
            //errorLogger.addHandler(dcErrorHandler);
            errorLogger.addHandler(emailErrorHandler);
        }
    }

    public void registerError(ErrorView view) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        view.exception().printStackTrace(pw);
        errorLogger.log(GLevel.Error, view.msg() + " -> " + view.exception().getMessage() + "\n" + sw);
    }

    @Comment.Getter
    public Logger getLogger() {
        return errorLogger;
    }
}