/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.email;
import net.gotzi.minestrum.utils.PropertyUtils;
import net.gotzi.minestrum.api.Bot;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class EmailBot extends Bot {

    private final Logger logger;
    private final Properties properties;
    private final Formatter formatter;


    public EmailBot(Logger logger, Formatter formatter, Properties properties) {
        this.logger = logger;
        this.properties = properties;
        this.formatter = formatter;
    }

    @Override
    public EmailBot start() {
        final Properties emailProperties = new Properties();
        emailProperties.put("mail.smtp.starttls.required", "true");
        emailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        emailProperties.put("mail.smtp.host", this.properties.getProperty("email_host"));
        emailProperties.put("mail.smtp.port", this.properties.getProperty("email_port"));
        emailProperties.put("mail.smtp.ssl.enable", "true");
        emailProperties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(emailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("email"),
                        PropertyUtils.clearHidingProperty(
                                properties.getProperty("email_password")
                        )
                );
            }
        });

        final Handler errorhandler = new ErrorLoggingHandler(session, this.logger, this.properties);
        errorhandler.setFormatter(formatter);

        setErrorhandler(errorhandler);
        return this;
    }

    @SuppressWarnings("empty")
    @Override
    public void stop() {}
}