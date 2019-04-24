package alex.home.shop.dto;

import alex.home.shop.domain.Category;
import alex.home.shop.domain.Product;
import java.io.Serializable;
import java.util.List;

public class ProductCategories implements Serializable {
    
    public final Product product;
    public final List<Category> allCategories;
    
    public ProductCategories(Product product, List<Category> allCategories) {
        this.product = product;
        this.allCategories = allCategories;
    }

    public Product getProduct() {
        return product;
    }

    public List<Category> getAllCategories() {
        return allCategories;
    }
    
}
