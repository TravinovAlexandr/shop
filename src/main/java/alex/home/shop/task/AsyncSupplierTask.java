package alex.home.shop.task;

import alex.home.shop.task.mediator.AsyncMethod;

public class AsyncSupplierTask implements AsyncTask {

    private final AsyncMethod asyncMethod;
    
    public AsyncSupplierTask(AsyncMethod asyncMethod) {
        this.asyncMethod = asyncMethod;
    }
    
    @Override
    public Object get() {
        if (asyncMethod == null || asyncMethod.isCallObjNull() || asyncMethod.isArgsNull()) {
            throw new IllegalArgumentException(asyncMethod == null ? "AsyncMethod asyncMethod = null " : "" 
                    .concat(asyncMethod.isCallObjNull() ? "CallObject ==  null  " : "")
                    .concat(asyncMethod.isArgsNull() ? "  CallObject args == null" : ""));
        }
        
        asyncMethod.initMethodArguments();
        return asyncMethod.usyncCall();    
    }

    @Override
    public Object call() throws Exception {
        throw new UnsupportedOperationException("Method is not support"); 
    }
    
}
