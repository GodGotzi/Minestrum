package at.gotzi.minestrum.email;

import at.gotzi.minestrum.api.logging.LogDefaultFormatter;
import at.gotzi.minestrum.utils.PropertyUtils;
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
        final Properties emailProperties = new Properties();
        emailProperties.put("mail.smtp.host", this.properties.getProperty("email_host"));
        emailProperties.put("mail.smtp.port", this.properties.getProperty("email_port"));
        emailProperties.put("mail.smtp.ssl.enable", "true");
        emailProperties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(emailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProperties.getProperty("email"),
                        PropertyUtils.clearHidingProperty(
                                emailProperties.getProperty("email_password"))
                );
            }
        });

        final Handler errorhandler = new ErrorLoggingHandler(session, this.logger, this.properties);
        errorhandler.setFormatter(new LogDefaultFormatter(false));

        setErrorhandler(errorhandler);
        return this;
    }

    @SuppressWarnings("empty")
    @Override
    public void stop() {}
}