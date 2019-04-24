package alex.home.shop.service;

import alex.home.shop.dao.CartDao;
import alex.home.shop.domain.Cart;
import alex.home.shop.exception.AdminException;
import java.sql.ResultSet;
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED) public String createCartGetId() {
        try {
            return String.valueOf(jdbcTemplate.query("INSERT INTO cart (stts) VALUES ('on') returning id;", (ResultSet rs, int i) -> 
                    rs.getLong("id")).stream().findFirst().orElseThrow(AdminException::new));
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }
    
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Boolean singleStrArgBoolRetIsUpdated(String query) {
      if (query == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException");
        }
      
      try {
          return jdbcTemplate.update(query) != 0;
      } catch (DataAccessException ex) {
          ex.printStackTrace();
          throw new AdminException(ex);
      }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateCart(Cart cart) {
        if (cart == null || cart.id == null || cart.clientEmail == null || cart.clientTelephone == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        try {
            jdbcTemplate.update("UPDATE cart SET name = ?, email = ?, telephone = ?, address = ?, client_wish = ? WHERE id = " + cart.id,
                    cart.clientName, cart.clientEmail, cart.clientTelephone, cart.clientAddress, cart.clientWish);
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
    public void singleStrArgVoidRet(String query) {
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
    public void vargsStrArgVoidRet(String ... query) {
        if (query == null || query.length == 0) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        try {
            jdbcTemplate.batchUpdate(query);
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
