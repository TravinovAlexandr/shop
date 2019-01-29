package alex.home.angular.sql.search;

    public class SearchElement {

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
