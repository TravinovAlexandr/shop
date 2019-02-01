package alex.home.angular.task.mediator;

public interface AsyncMethod {
    
    final String VOID_TYPE = "VOID";
    
    final ThreadLocal argsStorage = new ThreadLocal();
    
    void initMethodArguments();

    Object usyncCall();
    
    boolean isArgsNull();
    
    boolean isCallObjNull();
    
}
