//package alex.home.angular.sql.query;
//
//import alex.home.angular.dto.SearchQuery;
//import alex.home.angular.dto.SearchQuery.SearchField;
//import alex.home.angular.sql.PGMeta;
//import java.util.List;
//
//public class SqlQueryProduct implements SqlQuery {
//    
//    private final SearchQuery query;
//    
//    public SqlQueryProduct(SearchQuery query) {
//        this.query = query;
//    }
// 
//    @Override
//    public  String getQueryRow() {
//        if (query == null || query.limit == null || query.offset == null || query.searchQuery == null || query.searchQuery.isEmpty()){
//            return null;
//        }
//        
//        List<SearchField> fields = query.searchQuery;
//        StringBuilder sb = new StringBuilder("SELECT ");
//        StringBuilder countSubString = new StringBuilder("SELECT id FROM product WHERE ");
//        int categoryIndex = -1;
//        int fieldsSize = fields.size();
//        
//        for (int i= 0; i < fieldsSize; i++) {
//            if (fields.get(i).columnName.equals("categories") && fields.get(i).data != null  
//                    && fields.get(i).data[0] != null && fields.get(i).operator != null) {
//                categoryIndex = i;
//            }
//        }
//        
//        if (categoryIndex != -1) {
////            sb.append("DISTINCT p.*, COUNT(pp.id) FROM ").append(PGMeta.PRODUCT_TABLE).append(" p JOIN ").append(PGMeta.CATEGORY_PRODUCTS_TABLE)
////                    .append(" pc ON p.id = pc.product_id AND pc.category_id IN");
//            
//            int lastItertion = fields.get(categoryIndex).data.length -1;
//            
//            for (int i = 0; i <= lastItertion;  i++) {    
////                if (i != lastItertion) {
////                    sb.append(fields.get(categoryIndex).data[i]).append(",");
////                } else {
////                    sb.append(fields.get(categoryIndex).data[i]).append(" ) ");
////                }
//            }
//        }
//        
//        if (categoryIndex != 0) {
//            if (categoryIndex == -1) {
//                sb.append(" p.*, COUNT(pp.id) FROM ").append(PGMeta.PRODUCT_TABLE).append(" p ");
//            }
//            sb.append(" JOIN product pp ON pp.id = ANY(#) ").append(" WHERE ");
//
//            SearchField fht= null;
//            boolean isDataExist;
//            
//            for (int i = 0; i < fieldsSize; i++) {
//                isDataExist = false;
//                
//                if (i == categoryIndex) {
//                    continue;
//                }
//                
//                fht = fields.get(i);
//                
//                if (fht.data[0] != null && !fht.data[0].trim().equals("")) {
//                    isDataExist = true;
//                    
//                    if (">=<".equals(fht.operator)) {
//                        if (fht.data[1] == null || fht.data[1].trim().equals("")) {
//                            continue;
//                        }
//                        countSubString.append(fht.columnName).append(" >= ").append(fht.data[0]).append(" AND ")
//                                .append(fht.columnName).append(" < ").append(fht.data[1]);
//                        
//                        sb.append(" p.").append(fht.columnName).append(" >= ").append(fht.data[0]).append(" AND ").append(" p.")
//                                .append(fht.columnName).append(" < ").append(fht.data[1]);
//                    } else if ("Between".equals(fht.operator)){
//                        if (fht.data[1] == null || fht.data[1].trim().equals("")) {
//                            continue;
//                        }
//                        
//                        countSubString.append(fht.columnName).append(" BETWEEN '").append(fht.data[0]).append("' AND '").append(fht.data[1]).append("'");
//                        
//                        sb.append(" p.").append(fht.columnName).append(" BETWEEN '").append(fht.data[0]).append("' AND '").append(fht.data[1]).append("'");
//                    } else if ("text".equals(fht.type) || "textarea".equals(fht.type)){
//                        countSubString.append(fht.columnName).append("Like".equals(fht.operator) ? " LIKE '%" : " ='").append(fht.data[0])
//                                .append("Like".equals(fht.operator) ? "%'" : "'");
//                        
//                        sb.append(" p.").append(fht.columnName).append("Like".equals(fht.operator) ? " LIKE '%" : " ='").append(fht.data[0])
//                                .append("Like".equals(fht.operator) ? "%'" : "'");
//                    } else if ("number".equals(fht.type)) {
//                        countSubString.append(fht.columnName).append(fht.operator).append(fht.data[0]);
//                        
//                        sb.append(" p.").append(fht.columnName).append(fht.operator).append(fht.data[0]);
//                    } else if ("boolean".equals(fht.type)) {
//                        countSubString.append(fht.columnName).append("='").append(fht.data[0]).append("'");
//                        
//                        sb.append(" p.").append(fht.columnName).append("='").append(fht.data[0]).append("'");
//                    //single date type
//                    } else {
//                        countSubString.append(fht.columnName).append(" ").append(fht.operator).append("'").append(fht.data[0]).append("'");
//                        
//                        sb.append(" p.").append(fht.columnName).append(" ").append(fht.operator).append("'").append(fht.data[0]).append("'");
//                    }
//                }
//                
//                if (i != fieldsSize -1 && isDataExist) {
//                    countSubString.append(" AND ");
//                    
//                    sb.append(" AND ");
//                }
//            }
//       }
//        
//        if (String.format("SELECT * FROM %s WHERE ", PGMeta.PRODUCT_TABLE).equals(sb.toString())) {
//            return null;
//        }
//        
//        sb.append(" GROUP BY p.id LIMIT ").append(query.limit).append(" OFFSET ").append(query.offset).append(";");
//        
//        String queryRow = sb.toString();
//
//        return queryRow.replaceFirst("#", countSubString.toString());
//    }
//    
//}




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
        if (query == null || query.limit == null || query.offset == null || query.searchQuery == null || query.searchQuery.isEmpty()){
            return null;
        }
        
        List<SearchField> fields = query.searchQuery;
        StringBuilder sb = new StringBuilder("SELECT ! ");
        StringBuilder categoryJoin = new StringBuilder();
        int categoryIndex = -1;
        int fieldsSize = fields.size();
        
        for (int i= 0; i < fieldsSize; i++) {
            if (fields.get(i).columnName.equals("categories") && fields.get(i).data != null  
                    && fields.get(i).data[0] != null && fields.get(i).operator != null) {
                categoryIndex = i;
            }
        }
        
        if (categoryIndex != -1) {
            categoryJoin.append(" JOIN ").append(PGMeta.CATEGORY_PRODUCTS_TABLE).append(" pc ON p.id = pc.product_id AND pc.category_id IN");

            int lastItertion = fields.get(categoryIndex).data.length -1;
            
            for (int i = 0; i <= lastItertion;  i++) {    
                if (i != lastItertion) {
                    categoryJoin.append(fields.get(categoryIndex).data[i]).append(",");
                } else {
                    categoryJoin.append(fields.get(categoryIndex).data[i]).append(" ) ");
                }
            }
        }
       
        sb.append(" p.*, COUNT(pp.id) FROM ").append(PGMeta.PRODUCT_TABLE).append(" p ")
                .append(" JOIN product pp ON pp.id = ANY(#) ").append(" WHERE ");

        SearchField fht = null;
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

                    sb.append("@").append(fht.columnName).append(" >= ").append(fht.data[0]).append(" AND ").append(" @")
                            .append(fht.columnName).append(" < ").append(fht.data[1]);
                } else if ("Between".equals(fht.operator)) {
                    if (fht.data[1] == null || fht.data[1].trim().equals("")) {
                        continue;
                    }

                    sb.append(" @").append(fht.columnName).append(" BETWEEN '").append(fht.data[0]).append("' AND '").append(fht.data[1]).append("'");
                } else if ("text".equals(fht.type) || "textarea".equals(fht.type)) {
                    sb.append(" @").append(fht.columnName).append("Like".equals(fht.operator) ? " LIKE '%" : " ='").append(fht.data[0])
                            .append("Like".equals(fht.operator) ? "%'" : "'");
                } else if ("number".equals(fht.type)) {
                    sb.append(" @").append(fht.columnName).append(fht.operator).append(fht.data[0]);
                } else if ("boolean".equals(fht.type)) {
                    sb.append(" @").append(fht.columnName).append("='").append(fht.data[0]).append("'");
                    //single date type
                } else {
                    sb.append(" @").append(fht.columnName).append(" ").append(fht.operator).append("'").append(fht.data[0]).append("'");
                }
            }

            if (i != fieldsSize - 1 && isDataExist) {
                sb.append(" AND ");
            }
        }
        
        sb.append(" GROUP BY p.id LIMIT ").append(query.limit).append(" OFFSET ").append(query.offset).append(";");
        
        int whereIndex = sb.indexOf("WHERE") + 5;
        int groupByIndex = sb.indexOf("GROUP");
        String anySubstring = sb.substring(whereIndex, groupByIndex);
        String queryRow = sb.toString();
        
        anySubstring = " SELECT id FROM " + PGMeta.PRODUCT_TABLE + " WHERE " +  anySubstring.replaceAll("@", "");
        queryRow = queryRow.replaceAll("@", "p.");
        queryRow = queryRow.replaceFirst("#", anySubstring);
        
        if (categoryIndex != -1) {
            int joinIndex = queryRow.indexOf("JOIN");
            String queryWithCategory = queryRow.substring(0, joinIndex - 4);
            
            queryWithCategory = queryWithCategory + categoryJoin.toString() + queryRow.substring(joinIndex);
            queryRow = queryWithCategory.replaceFirst("!", "DISTINCT");
        } else {
            queryRow = queryRow.replaceFirst("!", "");
        }
        
        return queryRow;
    }
    
}