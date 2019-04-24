package alex.home.shop.utils.email;

import java.util.Date;

public class EmailData {

    public String to;
    public String subject;
    public String text;
    public String from;
    public String attachName;
    public String attachMimeType;
    public Date sentDate;
    public Object attachment;
    
    public EmailData() {}
    
    public EmailData(String to, String subject, String text, String from, Date sentDate) {
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.from = from;
        this.sentDate = sentDate;
    }
    
    public EmailData(String to, String subject, String text, String from, String attachName, String attachMimeType, Date sentDate, Object attachement) {
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.from = from;
        this.attachName = attachName;
        this.attachMimeType = attachMimeType;
        this.sentDate = sentDate;
        this.attachment = attachement;
    }
    
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public String getAttachMimeType() {
        return attachMimeType;
    }

    public void setAttachMimeType(String attachMimeType) {
        this.attachMimeType = attachMimeType;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

}
