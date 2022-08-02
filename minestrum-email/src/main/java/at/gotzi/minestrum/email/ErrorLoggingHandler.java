package at.gotzi.minestrum.email;


import at.gotzi.minestrum.utils.PropertyHelper;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ErrorLoggingHandler extends Handler {

    private final Session session;
    @SuppressWarnings("unused")
    private final Logger logger;
    private final Properties properties;

    public ErrorLoggingHandler(Session session, Logger logger, Properties properties) {
        this.session = session;
        this.logger = logger;
        this.properties = properties;
    }

    @Override
    public void publish(LogRecord record) {
        MimeMessage message = new MimeMessage(this.session);
        final String formattedString = getFormatter().format(record);

        try {
            message.setFrom(new InternetAddress(this.properties.getProperty("email")));
            message.addRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(
                            PropertyHelper.clearHidingProperty(
                                    this.properties.getProperty("email_error_addresses")
                            )
                    )
            );

            message.setSubject("Minestrum Error");
            message.setText(formattedString);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("empty")
    @Override
    public void flush() {}

    @SuppressWarnings("empty")
    @Override
    public void close() {}

}
