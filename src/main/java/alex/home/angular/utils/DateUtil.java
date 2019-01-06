package alex.home.angular.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;

public class DateUtil {
    
    private static final String DATE = "yyyy-MM-dd";
    private static final String TIME = "HH:mm:ss";
    private static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    
    public static String getCurrentDate() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE);
        return sdf.format(new Date().getTime());
    }
    
    public static String getCurrentTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat(TIME);
        return sdf.format(new Date().getTime());
    }
    
    public static String getCurrentDateTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME);
        return sdf.format(new Date().getTime());
    }
    
    public static String getDate(long td) {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE);
        return sdf.format(td);
    }
    
    public static String getTime(long td) {
        final SimpleDateFormat sdf = new SimpleDateFormat(TIME);
        return sdf.format(td);
    }
    
    public static String getDateTime(long td) {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME);
        return sdf.format(td);
    }
    
    public static String getCustomCurrentDateTime(@Nullable String formatPattern) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(String.valueOf(formatPattern));
            return sdf.format(new Date().getTime());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(new Date().getTime());
    } 
}
