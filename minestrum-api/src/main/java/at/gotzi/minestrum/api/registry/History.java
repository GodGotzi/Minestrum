package at.gotzi.minestrum.api.registry;

import at.gotzi.minestrum.api.format.Formatter;
import at.gotzi.minestrum.api.format.LineBuilder;

import java.util.LinkedList;

public abstract class History<T> extends LinkedList<HistoryView<T>> {

    private Formatter<T> formatter;

    public History(Formatter<T> formatter) {
        this.formatter = formatter;
    }

    public History() {}

    @Override
    public String toString() {
        HistoryView<T> next;
        LineBuilder lineBuilder = new LineBuilder();

        while (!isEmpty()) {
            next = peek();
            lineBuilder.addLine(formatter.format(next.getValue()));
        }

        return lineBuilder.toString();
    }

    public String clearHistory() {
        HistoryView<T> next;
        LineBuilder lineBuilder = new LineBuilder();

        while (!isEmpty()) {
            next = poll();
            lineBuilder.addLine(formatter.format(next.getValue()));
        }

        return lineBuilder.toString();
    }

    public String next() {
        if (!isEmpty())
            return peek().show() + "\n";
        return "\n";
    }

    public boolean addElement(T t) {
        HistoryView<T> historyView = new HistoryView<>(t, this.formatter);
        return super.add(historyView);
    }

    public void removeElement(T t) {
        super.stream().filter(historyView -> historyView.getValue() == t).findAny().ifPresent(super::remove);
    }

    public void setFormatter(Formatter<T> formatter) {
        this.formatter = formatter;
    }

    public Formatter<T> getFormatter() {
        return formatter;
    }
}
