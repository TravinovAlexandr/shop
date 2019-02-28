package alex.home.angular.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import javax.annotation.Nullable;

public class DateUtil {
    
    public static final String DATE = "yyyy-MM-dd";
    public static final String TIME = "HH:mm:ss";
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    
    public enum DWMY {
        DAY, WEEK, MONTH, YEAR
    }
    
    @Nullable
    public static Long getDWMYMillis(@Nullable DWMY type, @Nullable Integer qunt) {
        if (type != null && qunt != null) {
            LocalDateTime ldt = LocalDateTime.now();
            switch (type) {
                case DAY : return ldt.minusDays(qunt).toInstant(ZoneOffset.UTC).toEpochMilli();
                case WEEK : return ldt.minusWeeks(qunt).toInstant(ZoneOffset.UTC).toEpochMilli();
                case MONTH : return ldt.minusMonths(qunt).toInstant(ZoneOffset.UTC).toEpochMilli();
                default: return ldt.minusYears(qunt).toInstant(ZoneOffset.UTC).toEpochMilli();
            }
        }
        return null;
    } 
    
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
    
    public static String getDate(Long td) {
        if (td == null) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE);
        return sdf.format(td);
    }
    
    public static String getTime(Long td) {
        if (td == null) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(TIME);
        return sdf.format(td);
    }
    
    public static String getDateTime(Long td) {
        if (td == null) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME);
        return sdf.format(td);
    }
    
    public static String getDate(Date td) {
        if (td == null) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE);
        return sdf.format(td);
    }
    
    public static String getTime(Date td) {
        if (td == null) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(TIME);
        return sdf.format(td);
    }
    
    public static String getDateTime(Date td) {
        if (td == null) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME);
        return sdf.format(td);
    }
    
    public static String getDate(String td) {
        if (td == null) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE);
        return sdf.format(td);
    }
    
    public static String getTime(String td) {
        if (td == null) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(TIME);
        return sdf.format(td);
    }
    
    public static String getDateTime(String td) {
        if (td == null) {
            return null;
        }
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
    
    public static Timestamp getTimeStamp(Long mils) {
        return new Timestamp(mils);
    }
}
