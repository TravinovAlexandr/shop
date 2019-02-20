package alex.home.angular.dto;

import alex.home.angular.domain.Cart;
import java.io.Serializable;
import java.util.List;

public class SubmitContract implements Serializable {
    
    public Cart cart;
    public List<ProductInCart> products;
    
    public SubmitContract() {}
    
    public static class ProductInCart implements Serializable {
        
        public Long prodId;
        public Short quantInCart;
        
        public ProductInCart() {}

        public Long getProdId() {
            return prodId;
        }

        public void setProdId(Long prodId) {
            this.prodId = prodId;
        }

        public Short getQuantInCart() {
            return quantInCart;
        }

        public void setQuantInCart(Short quantInCart) {
            this.quantInCart = quantInCart;
        }
    }

    public List<ProductInCart> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInCart> products) {
        this.products = products;
    }
    
}
