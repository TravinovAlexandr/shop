package alex.home.angular.task;

import alex.home.angular.task.mediator.AsyncMethod;

public class AsyncCallableTask implements AsyncTask {
    
    private final AsyncMethod asyncMethod;
    
    public AsyncCallableTask(AsyncMethod asyncMethod) {
        this.asyncMethod = asyncMethod;
    }

    @Override
    public Object call() {
        if (asyncMethod == null || asyncMethod.isCallObjNull() || asyncMethod.isArgsNull()) {
            throw new IllegalArgumentException(asyncMethod == null ? "AsyncMethod asyncMethod = null " : "" 
                    .concat(asyncMethod.isCallObjNull() ? "CallObject ==  null  " : "")
                    .concat(asyncMethod.isArgsNull() ? "  CallObject args == null" : ""));
        }
        
        asyncMethod.initMethodArguments();
        return asyncMethod.usyncCall();
    }

    @Override
    public Object get() {
        throw new UnsupportedOperationException("Method is not support"); 
    }
    
}
