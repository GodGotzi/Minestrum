package net.gotzi.minestrum.api.registry.history;

import net.gotzi.minestrum.api.format.Formatter;
import net.gotzi.minestrum.api.format.LineBuilder;

import java.util.LinkedList;
import java.util.Stack;

public abstract class History<T> extends Stack<HistoryView<T>> {

    private Formatter<T> showFormatter;
    private Formatter<T> infoFormatter;
    private LineBuilder lineHistory;

    public History() {
        this.lineHistory = new LineBuilder();
    }

    @Override
    public String toString() {
        return lineHistory.toString();
    }

    @Override
    public void clear() {
        this.lineHistory = new LineBuilder();
        super.clear();
    }

    public String next() {
        if (!isEmpty())
            return peek().show() + "\n";
        return "\n";
    }

    public boolean addRawElement(T t) {
        HistoryView<T> historyView = new HistoryView<>(t, this.showFormatter);
        lineHistory.addLine(infoFormatter.format(historyView.getValue()));
        return super.add(historyView);
    }

    public void removeRawElement(T t) {
        super.stream().filter(historyView -> historyView.getValue() == t).findAny().ifPresent(super::remove);
    }

    public void setInfoFormatter(Formatter<T> infoFormatter) {
        this.infoFormatter = infoFormatter;
    }

    public void setShowFormatter(Formatter<T> showFormatter) {
        this.showFormatter = showFormatter;
    }

    public Formatter<T> getInfoFormatter() {
        return infoFormatter;
    }

    public Formatter<T> getShowFormatter() {
        return showFormatter;
    }
}
