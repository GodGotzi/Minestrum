package at.gotzi.minestrum.email;

import at.gotzi.api.template.logging.GDefaultFormatter;
import at.gotzi.minestrum.Minestrum;
import at.gotzi.minestrum.api.Bot;
import at.gotzi.minestrum.utils.PropertyHelper;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;
import java.util.logging.Handler;

public class EmailBot extends Bot<EmailBot> {

    private Session session;

    @Override
    public EmailBot start() {
        final Properties properties = new Properties();
        properties.put("mail.smtp.host", Minestrum.getInstance().getProperty("email_host"));
        properties.put("mail.smtp.port", Minestrum.getInstance().getProperty("email_port"));
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        this.session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Minestrum.getInstance().getProperty("email").toString(),
                        PropertyHelper.clearHidingProperty(
                                Minestrum.getInstance().getProperty("email_password").toString())
                );
            }
        });

        final Handler errorhandler = new EmailErrorHandler(this.session);
        errorhandler.setFormatter(new GDefaultFormatter(false));

        setErrorhandler(errorhandler);
        return this;
    }

    public Session getSession() {
        return session;
    }

}