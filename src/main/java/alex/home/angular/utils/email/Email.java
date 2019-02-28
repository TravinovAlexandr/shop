package alex.home.angular.utils.email;

public interface Email {
    
    void sendSimpleEmail(EmailData emailData);
    
    void sendEmailWithAttachments(EmailData emailData);
}
