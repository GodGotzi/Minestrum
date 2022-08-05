package net.gotzi.minestrum.api.registry.history;

import net.gotzi.minestrum.api.format.Formatter;
import net.gotzi.minestrum.api.view.StringView;

public class HistoryView<T> extends StringView {

    private final T t;

    private final Formatter<T> formatter;

    public HistoryView(T t, Formatter<T> formatter) {
        this.t = t;
        this.formatter = formatter;
    }

    @Override
    public String show() {
        return formatter.format(t);
    }

    public T getValue() {
        return t;
    }
}