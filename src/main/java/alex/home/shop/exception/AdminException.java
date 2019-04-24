package alex.home.shop.exception;

import java.io.Serializable;

public class AdminException extends RuntimeException {
    
    private final AdminCallback adminCallBack;
    
    public AdminException() {
        adminCallBack = new AdminCallback();
    }
    
    public AdminException(Throwable throwable) {
        adminCallBack = new AdminCallback();
        
        setExceptionName(throwable.getClass().getSimpleName());
        setSTrace(throwable);
    }
    
    public final void setExceptionName(String exceptionName) {
        adminCallBack.exceptionName = exceptionName;
    }
    
    public final void setMessage(String message) {
         adminCallBack.message = message;
    }
    
    public final void setCause(String cause) {
         adminCallBack.cause = cause;
    }
    
    public final void setSTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        
        collectSTrace(sb, throwable);
        
        adminCallBack.sTrace = sb.toString();
    }
    
    public final AdminException addExceptionName(String exceptionName) {
        adminCallBack.exceptionName = exceptionName;
        return this;
    }
    
    public final AdminException addMessage(String message) {
         adminCallBack.message = message;
         return this;
    }
    
    public final AdminException addCause(String cause) {
         adminCallBack.cause = cause;
         return this;
    }
    
    public final AdminException addSTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        
        collectSTrace(sb, throwable);
        
        adminCallBack.sTrace = sb.toString();
        return this;
    }
    
    private void collectSTrace(StringBuilder sb, Throwable throwable) {
        if (throwable == null) {
            return;
        }
        
        sb.append("<hr>").append(throwable.getMessage()).append("<hr>");
        
        for (StackTraceElement ste : throwable.getStackTrace()) {    
            sb.append(ste).append("<br>");
        }
        
        collectSTrace(sb, throwable.getCause());
    }
    
   public AdminCallback get() {
        return adminCallBack;
    } 
    
    public static class AdminCallback implements Serializable {
    
        private String exceptionName;
        private String message;
        private String cause;
        private String sTrace;

        public String getExceptionName() {
            return exceptionName;
        }

        public String getMessage() {
            return message;
        }

        public String getCause() {
            return cause;
        }

        public String getSTrace() {
            return sTrace;
        }
    }
}
