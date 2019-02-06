package alex.home.angular.dto;

import alex.home.angular.domain.Product;
import java.io.Serializable;

public class ProductWithCount extends Product implements Serializable  {
    
    public Integer count;

    public ProductWithCount() {}

    public Integer getCount() {
        return count;
    }
}
