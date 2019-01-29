package alex.home.angular.utils.properties;

import java.util.Properties;
import javax.annotation.Nullable;

public final class PropCache {
    
    private final Cache prodCashe = new Cache();

    public synchronized void invalidateProdCache() {
        invalidateCache(prodCashe);
    }

    public synchronized boolean initProdPropsCache(@Nullable Properties properties) {
        return initProps(properties, prodCashe);
    }
    
    public synchronized Properties getProductProps(int oldCacheVal) {
        return getProps(oldCacheVal, prodCashe);
    }
    
    private void invalidateCache(Cache cache) {
        cache.cacheVal = -1;
    }
    
    private final boolean initProps(@Nullable Properties properties, Cache cashe) {
        if (properties == null) {
            return false;
        }

        cashe.props = properties;
        cashe.cacheVal = properties.hashCode();
        return true;
    }

    private final Properties getProps(int oldCacheVal, Cache cashe) {
        if (cashe.cacheVal == oldCacheVal) {
            return cashe.props;
        }

        return null;
    }

    private static class Cache {

        private int cacheVal;
        private Properties props;

    }
    
}
