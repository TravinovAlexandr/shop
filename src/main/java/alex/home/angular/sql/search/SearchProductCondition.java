package alex.home.angular.sql.search;

import static alex.home.angular.sql.PGMeta.BOOLEAN_COND;
import static alex.home.angular.sql.PGMeta.CHAR_COND;
import static alex.home.angular.sql.PGMeta.DATE_TIME_COND;
import static alex.home.angular.sql.PGMeta.NUM_COND;
import static alex.home.angular.sql.PGMeta.PG_BIGINT;
import static alex.home.angular.sql.PGMeta.PG_BOOLEAN;
import static alex.home.angular.sql.PGMeta.PG_INT;
import static alex.home.angular.sql.PGMeta.PG_INVALID_ATTNAME;
import static alex.home.angular.sql.PGMeta.PG_REAL;
import static alex.home.angular.sql.PGMeta.PG_SMALLINT;
import static alex.home.angular.sql.PGMeta.PG_TEXT;
import static alex.home.angular.sql.PGMeta.PG_TIMESTAMP;
import static alex.home.angular.sql.PGMeta.PG_VARCHAR;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class SearchProductCondition implements SearchCondition<SearchElement, PGMetaColumn> {

    @Override @Nullable
    public List<SearchElement> getCondition(@Nullable List<PGMetaColumn> flds) {
        if (flds != null) {
            return fieldConverter(flds);
        }
        
        return null; 
    }
    
    @Nullable
    private static List<SearchElement>  fieldConverter(@Nullable List<PGMetaColumn> flds) {
        if (flds != null) {
            List<SearchElement> serchElems = new ArrayList<>();
            
            for (int i = 0; i < flds.size(); i++) {
                int j = -1;
                
                while(++j < PG_INVALID_ATTNAME.length) {
                    if (flds.get(i).attname.equals(PG_INVALID_ATTNAME [j])) {
                    flds.remove(i);
                    }  
                }
            }
            
            for(PGMetaColumn pf : flds) {
                SearchElement se = new SearchElement();
                
                if (pf.atttypid == PG_VARCHAR) {
                    se.type = "text";
                    se.conditions = CHAR_COND;
                } else if (pf.atttypid == PG_TEXT) {
                    se.type = "textarea";
                    se.conditions = CHAR_COND;
                } else if (pf.atttypid == PG_REAL || pf.atttypid == PG_BIGINT || pf.atttypid == PG_INT || pf.atttypid == PG_SMALLINT) {
                    se.type = "number";
                    se.conditions = NUM_COND;
                } else if (pf.atttypid ==PG_TIMESTAMP) {
                    se.type = "date";
                    se.conditions = DATE_TIME_COND;
                } else if (pf.atttypid == PG_BOOLEAN) {
                    se.type = "radio";
                    se.conditions = BOOLEAN_COND;
                }
                
                se.name = pf.attname;
                serchElems.add(se);
            }
           
            return serchElems;
        }
        return null; 
    }
    

}
