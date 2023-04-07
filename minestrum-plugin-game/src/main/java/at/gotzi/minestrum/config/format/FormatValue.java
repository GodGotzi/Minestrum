package at.gotzi.minestrum.config.format;

import lombok.Getter;
import lombok.Setter;

public class FormatValue<T> {

    @Getter
    private final FormatType type;

    @Getter
    @Setter
    private T format;

    public FormatValue(FormatType type) {
        this.type = type;
    }

    public FormatValue(FormatType type, T format) {
        this(type);

        this.format = format;
    }
}
