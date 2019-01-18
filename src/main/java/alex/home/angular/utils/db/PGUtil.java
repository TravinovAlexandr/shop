package alex.home.angular.utils.db;

import java.util.List;
import javax.annotation.Nullable;

public class PGUtil {
    
    /*
    public final static Integer VARCHAR = 1043;
    public final static Integer TEXT = 25;
    public final static Integer REAL = 700;
    public final static Integer BOOLEAN = 16;
    public final static Integer BIGINT = 20;
    public final static Integer TIMESTAMP = 1114;
    public final static Integer INT = 23;
    public final static Integer SMALLINT = 21;
    public final static Integer UUID = 2950;
    */
    
    public final static String INVALID_ATTNAME [] = new String [] { "tableoid", "cmax", "xmax", "cmin", "xmin", "ctid", "id", "img_id" };
    public final static String CHAR_COND [] = new String [] { "=", "like" };
    public final static String NUM_COND [] = new String [] { ">", "<", "=", "<>", ">= and <"};
    public final static String DATE_TIME_COND [] = new String [] { ">", "<", "beetwen" };
        
    @Nullable
    public static String [] getCond(@Nullable String type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "text" : return CHAR_COND;
            case "number" : return NUM_COND;
            case "date" : return DATE_TIME_COND;
            default : return null;
        }
    }
    
    @Nullable
    public static List<PGField> updatePGFieldsType(@Nullable List<PGField> flds) {
        if (flds != null) {
            deleteConstraint(flds);
            flds.forEach(f -> f.type = setFieldType(f.atttypid));
            return flds;
        }
        return null;
    }
    
    //return input type
    private static String setFieldType(@Nullable Integer code) {
        if (code != null) {
            if (code == 1043 || code == 25) {
                return "text";
            } else if (code == 700 || code == 20 || code == 23 || code == 21) {
                return "number";
            } else if (code ==1114) {
                return "date";
            }
        }
        return "UNDEFINED";
    }
    
    private static void deleteConstraint(List<PGField> flds) {
        for (int i = 0; i < flds.size(); i++) {
            int j = 0;
            for (j = 0; j < PGUtil.INVALID_ATTNAME.length; j++) {
                if (flds.get(i).attname.equals(PGUtil.INVALID_ATTNAME [j])) {
                    flds.remove(i);
                }  
            }
        }
    } 
    
    
}