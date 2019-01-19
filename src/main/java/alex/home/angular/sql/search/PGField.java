package alex.home.angular.sql.search;

public class PGField {
    
    public String  attname;
    public Integer atttypid;
    public String type;
        
    public PGField() {}
        
    public PGField(String  attname, Integer atttypid) {
            this.attname = attname;
            this.atttypid = atttypid;
        }
    
    }
