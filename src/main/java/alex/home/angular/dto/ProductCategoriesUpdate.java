package alex.home.angular.dto;

import java.io.Serializable;
import java.util.List;

public class ProductCategoriesUpdate implements Serializable{
    
    public Long productId;
    public List<Long> oldCategoriesId;
    public List<Long> newCategoriesId;
    
    public ProductCategoriesUpdate() {}

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public List<Long> getOldCategoriesId() {
        return oldCategoriesId;
    }

    public void setOldCategoriesId(List<Long> oldCategoriesId) {
        this.oldCategoriesId = oldCategoriesId;
    }

    public List<Long> getNewCategoriesId() {
        return newCategoriesId;
    }

    public void setNewCategoriesId(List<Long> newCategoriesId) {
        this.newCategoriesId = newCategoriesId;
    }
    
    
}
