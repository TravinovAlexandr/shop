package alex.home.angular.dao;

import alex.home.angular.domain.Cart;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDao {
    
    String createCartGetCookieUUID();
    
    Long updateCartGetId(Cart sc);
    
    void cartProductsMultyInsertion(String query);
    
    void singleStrArgVoidRet(String query, String arg);
    
    void addProduct(String uuid, Long prodId);
    
}
