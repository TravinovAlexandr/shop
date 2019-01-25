package alex.home.angular.utils.properties;

import java.util.concurrent.Callable;

public class PropLoaderTask<Properties> implements Callable<java.util.Properties> {

    private final PropLoader propertiesLoader;
    private final String path;
    
    public PropLoaderTask(PropLoader propertiesLoader, String path) {
        this.propertiesLoader = propertiesLoader;
        this.path = path;
    }
    
    @Override
    public java.util.Properties call() throws Exception {
        if (propertiesLoader == null || path == null) {
            throw new RuntimeException();
        }
        
        return propertiesLoader.load(path);
    }
    
}