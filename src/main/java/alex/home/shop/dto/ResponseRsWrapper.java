package alex.home.shop.dto;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

public class ResponseRsWrapper implements Serializable {
    
    private Object response;
    private String responseMessage;
    private String[] urls;
    
    public ResponseRsWrapper() {}
    
    public ResponseRsWrapper(Object response) {
        this.response = response;
    }
    
    public ResponseRsWrapper addHttpErrorStatus(HttpServletResponse resp, @Nullable Integer errStatus) {
        if (resp != null) {
            resp.setStatus((errStatus != null) ? errStatus : 500);
        }
        return this;
    }
    
    public ResponseRsWrapper addResponse(Object response) {
        this.response = response;
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

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
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
