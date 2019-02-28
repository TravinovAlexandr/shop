package alex.home.angular.utils;

import alex.home.angular.dto.SubmitContract;
import java.util.Arrays;
import java.util.List;

public class SqlUtil {
    
    private static <T extends Number> String getArray(List<T> lst, String castType, boolean isNum) {
        if (lst == null || castType == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder("ARRAY[").append(isNum ? "" : "'");
        int size = lst.size();
        
        while (size-- > 0) {
            sb.append(lst.get(size));
            if (size != 0) {
                sb.append(isNum ? "," : "',");
            }
        }

        return sb.append(isNum ? "" : "'").append(castType).toString();
    }
    
    public static String getINNumSequence(List<?> arg) {
        if (arg == null || arg.isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        int size = arg.size();
        
        for (int i = 0; i < size; i++) {
            if (i == size -1) {
                sb.append(arg.get(i));
            } else {
                sb.append(arg.get(i)).append(",");
            }
        }
        
        return sb.toString();
    }
    
    public static String getBigintArray(Long [] arg) {
      return getBigintArray(Arrays.asList(arg));  
    }

    public static String getBigintArray(List<Long> arg) {
          return getArray(arg, "]::BIGINT[]", true);
    }
    
    public static String getIntArray(Integer [] arg) {
      return getIntArray(Arrays.asList(arg));  
    }
    
    public static String getIntArray(List<Integer> arg) {
          return getArray(arg, "]::INT[]", true);
    }
    
    public  static String getMultyInsertValues(List<SubmitContract.ProductInCart> lpc, Long cartId) {
        if (lpc == null || cartId == null || lpc.isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        int size = lpc.size();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < lpc.get(i).quantInCart; j++) {
                sb.append("(").append(cartId).append(",").append(lpc.get(i).prodId).append(")");
                if (i != size - 1) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }
    
}
