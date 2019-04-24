package alex.home.shop.utils;

import alex.home.shop.dto.SubmitContract;
import java.util.Arrays;
import java.util.Collection;
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
    
    public static <T extends Number> String getINNumSequence(List<T> arg) {
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
    
    public  static <T extends Number> String getProductInCartMultyInsertValues(List<SubmitContract.ProductInCart> lpc, T cartId) {
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
    
//    public  static <T extends Number> String getMultyInsertValues(T [] id0, T id1) {
//        if (id1 == null || id0 == null || id0.length == 0) {
//            return null;
//        }
//        
//        StringBuilder sb = new StringBuilder();
//        
//        for (int i = 0; i < id0.length; i++) {
//                sb.append("(").append(id0[i]).append(",").append(id1).append(")");
//                if (i != id0.length - 1) {
//                    sb.append(",");
//                }
//            }
//        
//        return sb.toString();
//    }
    
    public  static <T, E extends Number> String getMultyInsertValues(T [] id0, E id1) {
        if (id1 == null || id0 == null || id0.length == 0) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < id0.length; i++) {
                sb.append("(").append(id0[i]).append(",").append(id1).append(")");
                if (i != id0.length - 1) {
                    sb.append(",");
                }
            }
        
        return sb.toString();
    }
    
    public static <T, E extends Number> String getMultyInsertValues(List<T> id0, E id1) {
        if (id1 == null || id0 == null || id0.isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < id0.size(); i++) {
                sb.append("(").append(id0.get(i)).append(",").append(id1).append(")");
                if (i != id0.size() - 1) {
                    sb.append(",");
                }
            }
        
        return sb.toString();
    }
}
