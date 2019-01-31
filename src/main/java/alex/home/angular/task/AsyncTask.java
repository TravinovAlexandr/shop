package alex.home.angular.task;

import java.util.function.Supplier;

public class AsyncTask implements Supplier {
    
    private final AsyncMethod asyncMethod;
    
    public AsyncTask(AsyncMethod asyncMethod) {
        this.asyncMethod = asyncMethod;
    }

    @Override
    public Object get() {
        if (asyncMethod == null) {
            throw new NullPointerException("AsyncMethod asyncMethod was not init.");
        }
        return asyncMethod.usyncCall();
    }
    
}
