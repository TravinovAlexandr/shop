package alex.home.shop.dto;

import alex.home.shop.domain.Product;
import java.io.Serializable;
import java.util.List;

public class ProductsCount implements Serializable {
    
    public List<Product> products;
    public Integer count;

    public ProductsCount() {}

    public ProductsCount(List<Product> products, Integer count) {
        this.products = products;
        this.count = count;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
}
