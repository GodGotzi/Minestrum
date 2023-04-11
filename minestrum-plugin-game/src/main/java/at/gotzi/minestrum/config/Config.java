package at.gotzi.minestrum.config;

import lombok.Getter;

import java.io.*;
import java.util.Properties;
import java.util.stream.Collectors;

public abstract class Config extends Properties {

    @Getter
    private File config;

    public Config(String configPath) {

        this.config = new File(configPath);

        try {
            if (!config.exists()) {
                createDefaultMessages(config);
            }

            FileReader reader = new FileReader(config);

            this.load(reader);
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
