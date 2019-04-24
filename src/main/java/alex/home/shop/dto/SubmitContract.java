package alex.home.shop.dto;

import alex.home.shop.domain.Cart;
import java.io.Serializable;
import java.util.List;

public class SubmitContract implements Serializable {
    
    public Cart cart;
    public List<ProductInCart> products;
    
    public SubmitContract() {}
    
    public static class ProductInCart implements Serializable {
        
        public Integer prodId;
        public Short quantInCart;
        
        public ProductInCart() {}

        public Integer getProdId() {
            return prodId;
        }

        public void setProdId(Integer prodId) {
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
