package starter.email;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    /**
     * Only for test
     */
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(SimpleMessage message) throws UnsupportedEncodingException, MessagingException {
        if (javaMailSender != null) {
            MimeMessageHelper helper = new MimeMessageHelper(javaMailSender.createMimeMessage(), false, "UTF-8");
    
            helper.setFrom(message.getFromEmail(), message.getFromPersonal());
            helper.setTo(message.getTo());
            helper.setSubject(message.getSubject());
            helper.setText(message.getBody(), true);
    
            javaMailSender.send(helper.getMimeMessage());
        }
    }

    @Async
    public Future<Boolean> sendEmailAsync(SimpleMessage message) {
        try {
            sendEmail(message);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            log.error("Error sending message", e);

            return new AsyncResult<>(false);
        }
    }
}
