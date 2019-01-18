package alex.home.angular.dao;

import alex.home.angular.domain.Cart;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDao {
    
    Cart selectCurrentCart(String  clientName);
    
    List<Cart> selectCartsClientsPagination(Integer limit, Integer offset, Cart.OrderStatus status); 
    
    Integer selectActiveCount();
    
    Integer selectStoppedCount();
    
    Integer selectEndedCount();
    
    boolean updateCartStatus(Long id, Cart.OrderStatus status);
    
    boolean initCart(String clientName);
    
    boolean deleteCart(Long id);
    
    boolean insertProductInCart(Long cartId, Long productId);
    
    boolean deleteProductFromCart(Long cartId, Long productId);
}
