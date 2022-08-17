package net.gotzi.minestrum.api.registry.history;

import java.io.*;

public class FileHistory<T> extends History<T> {

    private final File file;
    private OutputStreamWriter outputStreamWriter;

    public FileHistory(File file) throws IOException {
        this.file = file;
        this.outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
    }

    @Override
    public boolean addRawElement(T t) {
        final boolean ret = super.addRawElement(t);

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

    @Override
    public void clear() {
        try {
            outputStreamWriter.write("");
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.clear();
    }
}
