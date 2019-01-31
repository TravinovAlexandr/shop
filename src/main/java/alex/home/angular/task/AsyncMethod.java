package alex.home.angular.task;

public interface AsyncMethod {

    final ThreadLocal argsStorage = new ThreadLocal();
    
    void initMethodArguments(Object args);

    Object usyncCall();
}
