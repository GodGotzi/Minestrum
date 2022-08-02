package at.gotzi.minestrum.api.error;

import java.io.PrintWriter;
import java.io.StringWriter;

public record ErrorView(String msg, Exception exception) {

    public String stackTrace() {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        this.exception.printStackTrace(pw);
        return sw.toString();
    }

}
