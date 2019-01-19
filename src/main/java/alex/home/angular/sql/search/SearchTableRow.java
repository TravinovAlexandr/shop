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
import alex.home.angular.sql.search.SearchTableRow.SearchElement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class SearchTableRow implements TableRow<SearchElement, PGField> {

    @Override @Nullable
    public List<SearchElement> getRows(@Nullable List<PGField> flds) {
        if (flds != null) {
            return fieldConverter(flds);
        }
        return null; 
    }
    
    @Nullable
    private static List<SearchElement>  fieldConverter(@Nullable List<PGField> flds) {
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
            
            for(PGField pf : flds) {
                SearchElement se = new SearchElement();
                if (pf.atttypid == PG_VARCHAR || pf.atttypid == PG_TEXT) {
                    se.type = "text";
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
    
    public static class SearchElement {

        public String name;
        public String type;
        public String [] conditions;
        
        public SearchElement() {}
        
        public SearchElement(String name, String type, String [] conditions) {
            this.name = name;
            this.type = type;
            this.conditions = conditions;
        }

        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
        
        public String [] getConditions() {
            return conditions;
        }

        public void setConditions(String [] conditions) {
            this.conditions = conditions;
        }
    }
}
