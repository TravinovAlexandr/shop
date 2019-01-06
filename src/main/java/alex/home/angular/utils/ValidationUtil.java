package alex.home.angular.utils;

import java.security.Principal;
import javax.annotation.Nullable;
import org.springframework.security.core.Authentication;

public class ValidationUtil {
    
    public static boolean isCorrectNick(@Nullable String nick) {
        return nick != null ?  true :  false;
    }
    
    public static boolean isCorrectPassword(@Nullable String password) {
        return password != null ?  true :  false;
    }
    
    public static boolean isCorrectEmail(@Nullable String email) {
        return email != null ?  true :  false;
    }
    
    public static boolean isAdminAuthenticated(@Nullable Principal principal) {
        return principal == null;
    }
    
    public static boolean isFirstAdmin(@Nullable Authentication auth) {
        return getSingelAdminRole(auth).equals("1");
    }
    
    public static String getSingelAdminRole(@Nullable Authentication auth) {
        return (auth != null) ?  String.valueOf(auth.getAuthorities().toArray()[0]) : "null";
    }
    
}
