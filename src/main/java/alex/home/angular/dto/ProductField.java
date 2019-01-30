package alex.home.angular.dto;

import java.io.Serializable;

public class ProductField implements Serializable {
    
    public Long productId;
    public String columnName;
    public String value;
    
    public ProductField() {}
    
    public ProductField(Long productId, String columnName, String value) {
        this.productId = productId;
        this.columnName = columnName;
        this.value = value;
    }

    public Long getProductId() {
        return productId;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getValue() {
        return value;
    }
    
    
}
