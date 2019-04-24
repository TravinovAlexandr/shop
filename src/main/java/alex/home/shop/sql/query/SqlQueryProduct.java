package alex.home.shop.sql.query;

import alex.home.shop.dto.SearchQuery;
import alex.home.shop.dto.SearchQuery.SearchField;
import alex.home.shop.sql.PGMeta;
import java.util.List;

public class SqlQueryProduct implements SqlQuery {
    
    private final SearchQuery query;
    
    public SqlQueryProduct(SearchQuery query) {
        this.query = query;
    }
 
    @Override
    public  String getQueryRow() {
        if (query == null || query.limit == null || query.offset == null || query.searchQuery == null || query.searchQuery.isEmpty()){
            return null;
        }
        
        List<SearchField> fields = query.searchQuery;
        StringBuilder sb = new StringBuilder("SELECT p.* FROM product p ");
        StringBuilder categoryJoin = new StringBuilder();
        int categoryIndex = -1;
        int fieldsSize = fields.size();
        
        for (int i= 0; i < fieldsSize; i++) {
            if (fields.get(i).columnName.equals("categories") && fields.get(i).data != null) {
                if (fields.get(i).data.length != 0) {
                    categoryIndex = i;    
                } else {
                    fields.remove(i);
                    fieldsSize--;
                }
            }
        }
        
        if (categoryIndex != -1) {
            categoryJoin.append(" JOIN ").append(PGMeta.CATEGORY_PRODUCTS_TABLE).append(" pc ON p.id = pc.product_id AND pc.category_id IN(");

            int lastItertion = fields.get(categoryIndex).data.length -1;
            
            for (int i = 0; i <= lastItertion;  i++) {    
                if (i != lastItertion) {
                    categoryJoin.append(fields.get(categoryIndex).data[i]).append(",");
                } else {
                    categoryJoin.append(fields.get(categoryIndex).data[i]).append(" ) ");
                }
            }
        }
        
        sb.append(categoryJoin.toString()).append(" WHERE ");
       
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

                if (">=<".equals(fht.operator)) {
                    if (fht.data[1] == null || fht.data[1].trim().equals("")) {
                        continue;
                    }

                    sb.append("p.").append(fht.columnName).append(" >= ").append(fht.data[0]).append(" AND ").append(" p.")
                            .append(fht.columnName).append(" < ").append(fht.data[1]);
                } else if ("Between".equals(fht.operator)) {
                    if (fht.data[1] == null || fht.data[1].trim().equals("")) {
                        continue;
                    }

                    sb.append(" p.").append(fht.columnName).append(" BETWEEN '").append(fht.data[0]).append("' AND '").append(fht.data[1]).append("'");
                } else if ("text".equals(fht.type) || "textarea".equals(fht.type)) {
                    sb.append(" p.").append(fht.columnName).append("Like".equals(fht.operator) ? " ILIKE '" : " ='").append(fht.data[0]).append("Like".equals(fht.operator) ? "%'" : "'");
                } else if ("number".equals(fht.type)) {
                    sb.append(" p.").append(fht.columnName).append(fht.operator).append(fht.data[0]);
                } else if ("boolean".equals(fht.type)) {
                    sb.append(" p.").append(fht.columnName).append("='").append(fht.data[0]).append("'");
                } else {
                    sb.append(" p.").append(fht.columnName).append(" ").append(fht.operator).append("'").append(fht.data[0]).append("'");
                }
            }

            if (i != fieldsSize - ((categoryIndex == -1) ? 1 : 2) && isDataExist && !fht.columnName.equals("categories")) {
                sb.append(" AND ");
            }
        }
        
        sb.append(" GROUP BY (p.id) LIMIT ").append(query.limit).append(" OFFSET ").append(query.offset).append(";");
        
        return sb.toString();
    }
    
}