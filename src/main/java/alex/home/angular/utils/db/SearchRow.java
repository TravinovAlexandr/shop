package alex.home.angular.utils.db;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class SearchRow {

    @Nullable
    public static  List<SearchElement> getSearchElements(@Nullable List<PGField> flds) {
        if (flds != null) {
            List<SearchElement> serchElems = new ArrayList<>();
            PGUtil.updatePGFieldsType(flds);
            flds.forEach(f -> serchElems.add(new SearchElement(f.attname, f.type ,PGUtil.getCond(f.type))));
            return serchElems;
        }
        return null;
    }
          
    public static class SearchElement {

        public String name;
        public String type;
        public String [] conditions;
        
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
