package alex.home.shop.utils.email;

import alex.home.shop.utils.DateUtil;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSender implements Email{
    
    public JavaMailSender mailSender;

    public EmailSender() { }
    
    @Override
    public void sendSimpleEmail(EmailData emailData) {
        if (emailData == null || emailData.to == null || emailData.from == null  || emailData.subject == null || emailData.text == null) {
            throw new IllegalArgumentException("Null args.");
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailData.to);
            message.setSubject(emailData.subject);
            message.setText(emailData.text);
            message.setFrom(emailData.from);
            message.setSentDate(DateUtil.getCurrentTimestamp());
            mailSender.send(message);
        } catch (MailException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    @Override
    public void sendEmailWithAttachments(EmailData emailData) {
        if (emailData == null || emailData.to == null || emailData.from == null || emailData.subject == null || emailData.text == null 
                || emailData.attachMimeType == null || emailData.attachName == null || emailData.attachment == null) {
            throw new IllegalArgumentException("Null args.");
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(emailData.to);
            helper.setSubject(emailData.subject);
            helper.setText(emailData.text);
            helper.setFrom(emailData.from);
            helper.setSentDate(DateUtil.getCurrentTimestamp());
            
            DataSource dataSource = null;

            if (emailData.attachment.getClass() == byte[].class) {
                dataSource = new ByteArrayDataSource(byte[].class.cast(emailData.attachment), emailData.attachMimeType);
            } else {
                throw new IllegalArgumentException("Attachement class doesn't support");
            }
            
            helper.addAttachment(emailData.attachName, dataSource);
            mailSender.send(message);
        } catch (MessagingException | MailException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Autowired
    public void setMmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    
    
}
