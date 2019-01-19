package alex.home.angular.exception;

//Предназначение - проброс сообщения с дао на UI доступный администратору.
//Клиент логируется или же оповещается с js
public class AdminException extends RuntimeException {
    
    public String exceptionName;
    public String message = "";
    public String cause = "";
    
    public AdminException(String message) {
        super(message);
        this.message += message;
    }
    
    public AdminException(Throwable cause) {
        super(cause);
        this.exceptionName = cause.getClass().getSimpleName();
        this.cause += cause.getMessage();
    }
    
    public AdminException(String message, Throwable cause) { 
        super(message, cause);
        this.exceptionName = cause.getClass().getSimpleName();
        this.message += message;
        this.cause += cause.getMessage();
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }
    
    public void setMessage(String message) {
        if (!this.message.equals("")) {
            this.message += "_____";
        }
         this.message += message;
    }
    
    public void setCause(String cause) {
        if (!this.cause.equals("")) {
            this.cause += "_____";
        }
         this.cause += cause;
    }
    
}
