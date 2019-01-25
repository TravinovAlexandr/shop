package alex.home.angular.utils.properties;

public abstract class PropLoderFactory {
    
    public enum LoaderType {
        FS, CLOUD
    }
    
    PropLoader getPropLoader(LoaderType lt) {
        if (LoaderType.FS.equals(lt)) {
            return new PropFsLoader();
        } else if (LoaderType.CLOUD.equals(lt)) {
            return new PropCloudLoader();
        }
        
        return null;
    }
}
