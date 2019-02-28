package alex.home.angular.dto;

import java.io.Serializable;

public class ProductField implements Serializable {
    
    public Integer productId;
    public String columnName;
    public String value;
    
    public ProductField() {}
    
    public ProductField(Integer productId, String columnName, String value) {
        this.productId = productId;
        this.columnName = columnName;
        this.value = value;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getValue() {
        return value;
    }
    
    
}
