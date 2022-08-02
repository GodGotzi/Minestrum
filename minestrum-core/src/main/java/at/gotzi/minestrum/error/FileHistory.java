package at.gotzi.minestrum.error;

import at.gotzi.minestrum.api.error.ErrorView;
import at.gotzi.minestrum.api.registry.History;
import at.gotzi.minestrum.utils.TimeHelper;

import java.io.*;

public class FileHistory extends History<ErrorView> {

    private final OutputStreamWriter outputStreamWriter;

    public FileHistory(File file) throws IOException {
        this.outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
        super.setFormatter(this::format);
    }

    private String format(ErrorView view) {
        StringBuilder builder = new StringBuilder();
        String date = TimeHelper.getSimpleDate();

        builder.append("[").append(date)
                .append("]").append(" [Exception -> ")
                .append(view.exception().getClass().getSimpleName()).append("]")
                .append(" message -> ").append(view.msg());

        return builder.toString();
    }

    @Override
    public boolean addElement(ErrorView errorView) {
        final boolean ret = super.addElement(errorView);

        if (ret) {
            try {
                outputStreamWriter.append(next());
                outputStreamWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return ret;
    }
}
