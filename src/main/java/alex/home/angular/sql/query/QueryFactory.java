package alex.home.angular.sql.query;

import alex.home.angular.dto.SearchQuery;
import javax.annotation.Nullable;

public class QueryFactory<T> {
    
    public static enum TABLE {
        PRODUCT
    }
    
    @Nullable
    public SqlQuery getSquelQuery(T type, TABLE tableName) {
        if (type != null) {
            if (TABLE.PRODUCT.equals(tableName)) {
                return new SqlQueryProduct((SearchQuery) type);
            }
        }
        return null;
    }
}
