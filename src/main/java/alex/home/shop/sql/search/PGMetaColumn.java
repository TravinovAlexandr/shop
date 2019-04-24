package alex.home.shop.sql.search;

public class PGMetaColumn {
    
    public String  attname;
    public Integer atttypid;
    public String type;
        
    public PGMetaColumn() {}
        
    public PGMetaColumn(String  attname, Integer atttypid) {
            this.attname = attname;
            this.atttypid = atttypid;
        }
    
    }
