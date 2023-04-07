package at.gotzi.minestrum.config;

import java.io.*;
import java.util.Properties;
import java.util.stream.Collectors;

public abstract class Config extends Properties {

    public Config(String configPath) {

        File file = new File(configPath);

        try {
            if (!file.exists()) {
                createDefaultMessages(file);
            }

            this.load(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void createDefaultMessages(File config) throws IOException {
        config.createNewFile();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(config.getName())));
        String content = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        FileWriter writer = new FileWriter(config);
        writer.write(content);
        writer.close();
    }
}
