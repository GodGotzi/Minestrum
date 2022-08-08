package net.gotzi.minestrum.error;

import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.ano.Comment;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.api.format.Formatter;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.api.registry.history.HistoryView;
import net.gotzi.minestrum.api.logging.MinestrumLogger;
import net.gotzi.minestrum.utils.FileUtils;
import net.gotzi.minestrum.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class ErrorHandler {

    private final Minestrum minestrum;
    private final Logger errorLogger;
    private final ErrorRegistry registry;

    private final Formatter<ErrorView> showFormatter = this::showFormat;
    private final Formatter<ErrorView> infoFormatter = this::infoFormat;

    @Comment.Constructor
    public ErrorHandler(Minestrum minestrum, Logger logger) {
        this.minestrum = minestrum;
        this.errorLogger = logger;

        File file = new File(this.minestrum.getErrorFolder().getPath() + "/" +
                TimeUtils.getSimpleDateForFile() + ".log");
        FileUtils.initFile(file);

        this.registry = new ErrorRegistry(file);

        try {
            loadHandlers();
        } catch (IOException e) {
            minestrum.getLogger().warning("Could not start ErrorHandler -> " + e.getMessage());
        }

        this.registry.getHistory().setShowFormatter(showFormatter);
        this.registry.getHistory().setInfoFormatter(infoFormatter);
    }

    @Comment.Init
    private void loadHandlers() throws IOException {
        final Handler dcErrorHandler = minestrum.getDiscordBot().getErrorhandler();
        final Handler emailErrorHandler = minestrum.getEmailBot().getErrorhandler();

        if (!Minestrum.DEBUG) {
            errorLogger.addHandler(dcErrorHandler);
            errorLogger.addHandler(emailErrorHandler);
        }
    }

    private String showFormat(ErrorView view) {
        StringBuilder builder = new StringBuilder();
        String date = TimeUtils.getSimpleDate();
        String threadName = Thread.currentThread().getName();

        builder.append("[").append(date)
                .append("]").append(" [Thread -> ").append(threadName).append("]").append(" [Exception -> ")
                .append(view.exception().getClass().getSimpleName()).append("]")
                .append(" message -> ").append(view.msg()).append(" StackTrace -> \n").append(view.stackTrace());

        return builder.toString();
    }

    private String infoFormat(ErrorView view) {
        StringBuilder builder = new StringBuilder();
        String date = TimeUtils.getSimpleDate();
        String threadName = Thread.currentThread().getName();

        builder.append("[").append(date)
                .append("]").append(" [Thread -> ").append(threadName).append("]").append(" [Exception -> ")
                .append(view.exception().getClass().getSimpleName()).append("]")
                .append(" message -> ").append(view.msg());

        return builder.toString();
    }

    public void registerError(ErrorView view) {
        errorLogger.log(LogLevel.Error, view.msg() + " -> " + view.exception().getMessage() + "\n" + view.stackTrace());
        this.registry.register(view);
    }

    public ErrorRegistry getRegistry() {
        return this.registry;
    }

    public List<HistoryView<ErrorView>> getHistory() {
        return Collections.unmodifiableList(this.registry.getHistory());
    }

    public Formatter<ErrorView> getInfoFormatter() {
        return infoFormatter;
    }

    public Formatter<ErrorView> getShowFormatter() {
        return showFormatter;
    }
}
