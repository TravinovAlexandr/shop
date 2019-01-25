package alex.home.angular.dto;

import alex.home.angular.domain.Product;
import java.io.Serializable;

public class ProductSearchTable extends Product implements Serializable  {
    
    public Integer count;

    public ProductSearchTable() {}

    public Integer getCount() {
        return count;
    }
}
