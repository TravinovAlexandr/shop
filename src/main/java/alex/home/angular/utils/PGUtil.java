package alex.home.angular.utils;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

public class PGUtil {
    
    @Nullable
    private static final <T extends Number> String getArray(List<T> lst, String castType, boolean isNum) {
        if (lst == null || castType == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder("ARRAY[").append(isNum ? "" : "'");
        int size = lst.size();
        
        while (size-- > 0) {
            if (size != 0) {
                sb.append(isNum ? "," : "',");
            }
        }
        
        return sb.append(isNum ? "" : "'").append(castType).toString();
    }
    
    @Nullable
    public static final String getBigintArray(Long[] arg) {
      return getBigintArray(Arrays.asList(arg));  
    }
    
    @Nullable
    public static final String getBigintArray(List<Long> arg) {
          return getArray(arg, "]::BIGINT[]", true);
    }
    
    
}
