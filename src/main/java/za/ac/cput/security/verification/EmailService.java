package za.ac.cput.security.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends verification / password-reset codes by email over SMTP.
 *
 * In development point spring.mail.host/port at Mailpit (localhost:1025) and read the
 * messages at http://localhost:8025. If no mail server is configured or reachable, the
 * message is written to the server log instead so the flow can still be completed locally.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${app.mail.from:no-reply@techvault.local}") String from) {
        this.mailSenderProvider = mailSenderProvider;
        this.from = from;
    }

    public void sendVerificationCode(String to, String code, int validMinutes) {
        send(to, "Verify your TechVault account",
                "Welcome to TechVault!\n\nYour verification code is: " + code
                        + "\n\nIt expires in " + validMinutes + " minutes. "
                        + "If you did not register, you can ignore this email.");
    }

    public void sendPasswordResetCode(String to, String code, int validMinutes) {
        send(to, "Your TechVault password reset code",
                "Your password reset code is: " + code
                        + "\n\nIt expires in " + validMinutes + " minutes. "
                        + "If you did not request a reset, you can ignore this email.");
    }

    private void send(String to, String subject, String body) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.warn("[EmailService] No mail server configured. Email to {} ({}):\n{}", to, subject, body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            sender.send(message);
        } catch (Exception ex) {
            log.warn("[EmailService] Could not send email to {} ({}). Falling back to log:\n{}\nReason: {}",
                    to, subject, body, ex.getMessage());
        }
    }
}

