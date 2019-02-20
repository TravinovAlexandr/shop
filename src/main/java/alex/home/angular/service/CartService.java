package alex.home.angular.service;

import alex.home.angular.dao.CartDao;import alex.home.angular.domain.Cart;
import alex.home.angular.exception.AdminException;
import alex.home.angular.utils.DateUtil;
import java.sql.ResultSet;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService implements CartDao {
    
    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String createCartGetCookieUUID() {
        try {
//            UUID cartUUID = UUID.randomUUID();
//            jdbcTemplate.update("INSERT INTO cart (cookie, startday, stts) VALUES (?,?,"+ "'on'" +");", cartUUID, DateUtil.getCurrentTimestamp());
//            return cartUUID.toString();

            return String.valueOf(jdbcTemplate.query("INSERT INTO cart (stts) VALUES ('on') returning id;", (ResultSet rs, int i) -> 
                    rs.getLong("id")).stream().findFirst().orElseThrow(AdminException::new));
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Long updateCartGetId(Cart cart) {
        if (cart == null || cart.cookie == null || cart.email == null || cart.telephone == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        try {
            //UPDATE cart SET name = ?, email = ?, telephone = ?, address = ?, client_wish = ? WHERE id = cart.id;
            String query = "WITH up AS (UPDATE cart SET name = ?, email = ?, telephone = ?, address = ?, client_wish = ? WHERE cookie = CAST(? AS UUID)) "
                + "SELECT id FROM cart WHERE cookie = CAST(? AS UUID);";
            return jdbcTemplate.query(query, new Object[] { cart.name, cart.email, cart.telephone, cart.address, cart.clientWish, cart.cookie, cart.cookie }, (ResultSet rs, int i) -> rs.getLong("id")).stream().findFirst().orElse(-1L);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void cartProductsMultyInsertion(String query) {
        if (query == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        try {
            jdbcTemplate.update(query);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addProduct(String uuid, Long prodId) {
        if (prodId == null || uuid == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        try {
            jdbcTemplate.update("INSERT INTO cart_orders VALUES((SELECT id FROM cart WHERE cookie = '?'), " + prodId + ")", uuid);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void singleStrArgVoidRet(String query, String arg) {
            if (query == null || arg == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        try {
            jdbcTemplate.update(query, arg);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



}
