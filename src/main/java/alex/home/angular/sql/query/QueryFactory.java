package alex.home.angular.sql.query;

import alex.home.angular.dto.SearchQuery.SearchField;
import java.util.List;
import javax.annotation.Nullable;

public class QueryFactory {
    
    public static enum TABLE {
        PRODUCT
    }
    
    @Nullable
    public SqlQuery getSquelQuery(List<SearchField> fields, TABLE tableName) {
        if (fields != null) {
            if (TABLE.PRODUCT.equals(tableName)) {
                return new SqlQueryProduct(fields);
            }
        }
        return null;
    }
}
