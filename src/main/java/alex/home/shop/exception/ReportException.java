package alex.home.shop.exception;

public class ReportException extends RuntimeException {
    
    public ReportException() {}

    public ReportException(String message) {
        super(message);
    }

    public ReportException(Throwable cause) {
        super(cause);
    }
    
}
