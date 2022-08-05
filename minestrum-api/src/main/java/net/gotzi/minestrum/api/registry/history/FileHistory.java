package net.gotzi.minestrum.api.registry.history;

import java.io.*;

public class FileHistory<T> extends History<T> {

    private final OutputStreamWriter outputStreamWriter;

    public FileHistory(File file) throws IOException {
        this.outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
    }

    @Override
    public boolean addElement(T t) {
        final boolean ret = super.addElement(t);

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
