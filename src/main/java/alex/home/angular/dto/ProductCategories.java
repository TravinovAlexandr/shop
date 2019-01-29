package alex.home.angular.dto;

import alex.home.angular.domain.Category;
import alex.home.angular.domain.Product;
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
