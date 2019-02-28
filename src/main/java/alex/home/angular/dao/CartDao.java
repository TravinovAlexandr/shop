package alex.home.angular.dao;

import alex.home.angular.domain.Cart;
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
