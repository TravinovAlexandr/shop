package alex.home.shop.utils.email;

public interface Email {
    
    void sendSimpleEmail(EmailData emailData);
    
    void sendEmailWithAttachments(EmailData emailData);
}
