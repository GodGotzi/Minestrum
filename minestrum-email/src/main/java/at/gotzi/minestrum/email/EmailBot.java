package at.gotzi.minestrum.email;

import at.gotzi.api.logging.GDefaultFormatter;
import at.gotzi.minestrum.utils.PropertyHelper;
import at.gotzi.minestrum.api.Bot;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class EmailBot extends Bot {

    private final Logger logger;

    private final Properties properties;

    public EmailBot(Logger logger, Properties properties) {
        this.logger = logger;
        this.properties = properties;
    }

    @Override
    public EmailBot start() {
        final Properties properties = new Properties();
        properties.put("mail.smtp.host", this.properties.getProperty("email_host"));
        properties.put("mail.smtp.port", this.properties.getProperty("email_port"));
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("email"),
                        PropertyHelper.clearHidingProperty(
                                properties.getProperty("email_password"))
                );
            }
        });

        final Handler errorhandler = new ErrorLoggingHandler(session, this.logger, this.properties);
        errorhandler.setFormatter(new GDefaultFormatter(false));

        setErrorhandler(errorhandler);
        return this;
    }

    @SuppressWarnings("empty")
    @Override
    public void stop() {}
}