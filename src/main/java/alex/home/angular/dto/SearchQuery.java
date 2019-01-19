package alex.home.angular.dto;

import java.io.Serializable;
import java.util.List;
import org.springframework.util.AutoPopulatingList;

public class SearchQuery implements Serializable {

    public List<SearchField> searchQuery = new AutoPopulatingList<>(SearchField.class);
    
    public static class SearchField {
    
        public String columnName;
        public String operator;
        public String type;
        public String [] data;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String[] getData() {
            return data;
        }

        public void setData(String[] data) {
            this.data = data;
        }    
    }

    public List<SearchField> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(List<SearchField> searchQuery) {
        this.searchQuery = searchQuery;
    }
}
