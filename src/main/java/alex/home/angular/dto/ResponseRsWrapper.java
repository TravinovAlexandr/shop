package alex.home.angular.dto;

import java.io.Serializable;
import org.springframework.http.HttpStatus;

public class ResponseRsWrapper implements Serializable {
    
    private Object message;
    private String responseMessage;
    private String[] urls;
    
    public ResponseRsWrapper() {}
    
    public ResponseRsWrapper(Object message) {
        this.message = message;
    }
    
    public ResponseRsWrapper addMessage(Object message) {
        this.message = message;
        return this;
    }
    
    public ResponseRsWrapper addResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }
    
    public ResponseRsWrapper addResponseMessage(HttpStatus responseMessage) {
        this.responseMessage = (responseMessage != null) ? responseMessage.toString() : null;
        return this;
    }
    
    public ResponseRsWrapper addUrls(String[] urls) {
        this.urls = urls;
        return this;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }
   
}
