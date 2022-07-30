package at.gotzi.minestrum.email;

import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.utils.PropertyHelper;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class EmailErrorHandler extends Handler {

    private final Session session;

    public EmailErrorHandler(Session session) {
        this.session = session;
    }

    @Override
    public void publish(LogRecord record) {
        MimeMessage message = new MimeMessage(this.session);
        final String formattedString = getFormatter().format(record);

        try {
            message.setFrom(new InternetAddress(Minestrum.getInstance().getProperty("email").toString()));
            message.addRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(
                            PropertyHelper.clearHidingProperty(
                                    Minestrum.getInstance().getProperty("email_error_addresses").toString()
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
