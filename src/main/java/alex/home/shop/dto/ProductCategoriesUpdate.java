package alex.home.shop.dto;

import java.io.Serializable;
import java.util.List;

public class ProductCategoriesUpdate implements Serializable{
    
    public Integer productId;
    public List<Integer> oldCategoriesId;
    public List<Integer> newCategoriesId;
    
    public ProductCategoriesUpdate() {}

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<Integer> getOldCategoriesId() {
        return oldCategoriesId;
    }

    public void setOldCategoriesId(List<Integer> oldCategoriesId) {
        this.oldCategoriesId = oldCategoriesId;
    }

    public List<Integer> getNewCategoriesId() {
        return newCategoriesId;
    }

    public void setNewCategoriesId(List<Integer> newCategoriesId) {
        this.newCategoriesId = newCategoriesId;
    }
    
    
}
