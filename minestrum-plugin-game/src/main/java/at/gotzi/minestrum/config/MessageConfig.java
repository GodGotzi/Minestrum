package at.gotzi.minestrum.config;

import at.gotzi.minestrum.config.format.FormatValue;

public class MessageConfig extends Config {

    public MessageConfig() {
        super("plugins//Minestrum//messages.properties");
    }

    public String getMessage(String key, FormatValue... formats) {
        String property = this.getProperty(key);

        for (FormatValue format : formats) {
            property = property.replaceAll("§" + format.getType().name() + "§", String.valueOf(format.getFormat()));
        }

        return property;
    }

}
