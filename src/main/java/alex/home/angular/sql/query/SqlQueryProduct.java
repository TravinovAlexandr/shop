package alex.home.angular.sql.query;

import alex.home.angular.dto.SearchQuery;
import alex.home.angular.dto.SearchQuery.SearchField;
import alex.home.angular.sql.PGMeta;
import java.util.List;

public class SqlQueryProduct implements SqlQuery {
    
    private final SearchQuery query;
    
    public SqlQueryProduct(SearchQuery query) {
        this.query = query;
    }
 
    @Override
    public  String getQueryRow() {
        if (query == null || query.limit == null || query.offset == null 
                || query.searchQuery == null || query.searchQuery.isEmpty()){
            return null;
        }
        
        List<SearchField> fields = query.searchQuery;    
        StringBuilder sb = new StringBuilder("SELECT ");
        int categoryIndex = -1;
        int fieldsSize = fields.size();
        
        for (int i= 0; i < fieldsSize; i++) {
            if (fields.get(i).columnName.equals("categories") 
                    && fields.get(i).data != null  && fields.get(i).data[0] != null
                    && fields.get(i).operator != null) {
                categoryIndex = i;
            }
        }
        
        if (categoryIndex != -1) {
            sb.append("DISTINCT * FROM ")
                    .append(PGMeta.PRODUCT_TABLE)
                    .append(" p INNER JOIN ")
                    .append(PGMeta.CATEGORY_PRODUCTS_TABLE)
                    .append(" pc ON p.id = pc.product_id AND pc.category_id IN");
            
            int lastItertion = fields.get(categoryIndex).data.length -1;
            for (int i = 0; i <= lastItertion;  i++) {
                
                if (i != lastItertion) {
                    sb.append(fields.get(categoryIndex).data[i]).append(",");
                } else {
                    sb.append(fields.get(categoryIndex).data[i]).append(" ) ");
                }
            }
        }
        
        if (categoryIndex != 0) {
            if (categoryIndex == -1) {
                sb.append("* FROM ")
                        .append(PGMeta.PRODUCT_TABLE);
            }
            sb.append(" WHERE ");
            
            SearchField fht;
            boolean isDataExist;
            
            for (int i = 0; i < fieldsSize; i++) {
                isDataExist = false;
                if (i == categoryIndex) {
                    continue;
                }
                
                fht = fields.get(i);
                
                if (fht.data[0] != null && !fht.data[0].trim().equals("")) {
                    isDataExist = true;
                    if (">= And <".equals(fht.operator)) {
                        if (fht.data[1].equals("")) {
                            continue;
                        }
                        sb.append(fht.columnName)
                                .append(" >= ")
                                .append(fht.data[0])
                                .append(" AND ")
                                .append(fht.columnName)
                                .append(" < ")
                                .append(fht.data[1]);
                    } else if ("Between".equals(fht.operator)){
                        if (fht.data[1].equals("")) {
                            continue;
                        }
                        sb.append(fht.columnName)
                                .append(" BETWEEN '")
                                .append(fht.data[0])
                                .append("' AND '")
                                .append(fht.data[1])
                                .append("'");
                    } else if ("text".equals(fht.type)){
                        sb.append(fht.columnName)
                                .append("Like".equals(fht.operator) ? " LIKE '%" : " ='")
                                .append(fht.data[0])
                                .append("Like".equals(fht.operator) ? "%'" : "'");
                    } else if ("number".equals(fht.type)){
                        sb.append(fht.columnName)
                                .append(fht.operator)
                                .append(fht.data[0]);
                    } else if ("boolean".equals(fht.type)) {
                        sb.append(fht.columnName)
                                .append("='")
                                .append(fht.data[0])
                                .append("'");
                    //single date type
                    } else {
                        sb.append(fht.columnName)
                                .append(" ")
                                .append(fht.operator)
                                .append("'")
                                .append(fht.data[0])
                                .append("'");
                    }
                }
                
                if (i != fieldsSize -1 && isDataExist) {
                    sb.append(" AND ");
                }  
            }
       }
        
        if (String.format("SELECT * FROM %s WHERE ", PGMeta.PRODUCT_TABLE).equals(sb.toString())) {
            return null;
        }
        
        sb.append(" LIMIT ")
                .append(query.limit)
                .append(" OFFSET ")
                .append(query.offset)
                .append(";");
        
        return sb.toString();
    }
    
}