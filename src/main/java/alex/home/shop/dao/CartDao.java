package alex.home.shop.dao;

import alex.home.shop.domain.Cart;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDao {
    
    String createCartGetId();
    
    Boolean singleStrArgBoolRetIsUpdated(String query);
    
    void updateCart(Cart sc);
    
    void cartProductsMultyInsertion(String query);
    
    void singleStrArgVoidRet(String query);
    
    void vargsStrArgVoidRet(String ... query);
}
