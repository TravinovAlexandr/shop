package alex.home.angular.utils;

import java.util.UUID;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookiesUtil {
    
    private static final String COOKIE_KEY = "AL_SH_KEY_2019";
    
    public static String getCookiesKey() {
        return COOKIE_KEY;
    }
    
    public static  boolean  hasClientCookie(@Nullable HttpServletRequest request) {
        if (request != null) {
            for (Cookie ck : request.getCookies()) {
               if (COOKIE_KEY.equals(ck.getName())) {
                   return true;
               }
            }
        }
        return false;
    }
    
    @Nullable
    public static String setCookieGetUUID(@Nullable HttpServletResponse response) {
        if (response != null) {
            String uuid = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(COOKIE_KEY, uuid);
            cookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(cookie);
            return uuid;
        }
        return null;
    }
    
    @Nullable
    public static String getCookie(@Nullable HttpServletRequest request) {
        if (request != null) {
            for (Cookie ck : request.getCookies()) {
                if (COOKIE_KEY.equals(ck.getName())) {
                    return ck.getValue();
                }
            }
        }
        return null;
    }
    
    public static boolean deleteCookie(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response) {
        if (request != null && response != null) {
            Cookie cookie = null;
            for (Cookie ck : request.getCookies()) {
                if (COOKIE_KEY.equals(ck.getName())) {
                    cookie = ck;
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return true;
                }
            }
        }
        return false;
    }
    
}