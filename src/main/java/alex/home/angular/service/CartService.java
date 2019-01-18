package alex.home.angular.service;

import alex.home.angular.dao.CartDao;
import alex.home.angular.domain.Cart;
import alex.home.angular.domain.Client;
import alex.home.angular.domain.Product;
import java.sql.ResultSet;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService implements CartDao {
    
    private JdbcTemplate jdbcTemplate;

    @Override @Nullable @Transactional 
    public Cart selectCurrentCart(@Nullable String clientCookie) {
        if (clientCookie != null) {
            try {
                String cartSql = "SELECT id FROM cart WHERE stts = 'on' AND client_id = (SELECT id FROM client WHERE name = ?);".intern();
                Cart cart = jdbcTemplate.queryForObject(cartSql, new Object[]{ clientCookie }, (ResultSet rs, int i) -> new Cart(rs.getLong(1)));
                if (cart == null) {
                    return null;
                }
                String prodSql = "SELECT * FROM product p INNER JOIN cart_orders cp ON p.id = cp.product_id AND cp.cart_id = ;".intern() + cart.id;
                List<Product> prdcts = jdbcTemplate.query(prodSql, (ResultSet rs, int i)
                        -> new Product(rs.getLong("id"), rs.getString("name"), rs.getString("sescription"), rs.getDouble("price")));
                cart.products = prdcts;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public List<Cart> selectCartsClientsPagination(@Nullable Integer limit, @Nullable Integer offset,@Nullable Cart.OrderStatus status) {
        if (limit != null && offset != null && status != null) {
            try {
            String sql = "SELECT crt.id,crt.startday, crt.endday,crt.description, cl.id AS clid, cl.name, cl.email, cl.mobile, cl.home, cl.address "
                    + "FROM cart crt INNER JOIN client cl ON (crt.client_id = cl.id) WHERE crt.stts = '".intern() + status + "' LIMIT " + limit +  " OFFSET " + limit * offset;
            List<Cart> crts = jdbcTemplate.query(sql, ((rs, i) -> {
                Cart cart = new Cart();
                cart.addId(rs.getLong("id")).addStartDay(rs.getDate("startdate"))
                        .addEndDay(rs.getDate("enddate")).addDesc(rs.getString("description"));
                Client client = new Client();
                client.addId(rs.getLong("id")).addName(rs.getString("name")).addEmail(rs.getString("email"))
                        .addMobilePhone(rs.getString("mobile")).addHomePhone(rs.getString("home")).addAddress(rs.getString("address"));                
                cart.client = client;
                return cart; 
            }));
            return crts;
            
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    private Integer selectCount(@Nullable String sql) {
        try {
            return jdbcTemplate.query(sql, (ResultSet rs) -> rs.getInt(1));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override @Nullable
    public Integer selectActiveCount() {
        return selectCount("SELECT COUNT(stts) FROM cart WHERE stts = 'on';".intern());
    }
    
    @Override
    public Integer selectStoppedCount() {
        return selectCount("SELECT COUNT(stts) FROM cart WHERE stts = 'stop';".intern());
    }
    
    @Override
    public Integer selectEndedCount() {
        return selectCount("SELECT COUNT(stts) FROM cart WHERE stts = 'off';".intern());
    }

    @Override
    public boolean updateCartStatus(@Nullable Long id, @Nullable Cart.OrderStatus status) {
        if (id != null && status != null) {
            try {
                String sql = "UPDATE cart SET stts = ?;".intern();
                return jdbcTemplate.update(sql, status) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    /*
    CREATE OR REPLACE FUNCTION INIT_CART(clcookie VARCHAR) 
    RETURNS INT AS $$ 
    DECLARE
        cur BIGINT;
    BEGIN
        IF clcookie IS NULL THEN
            RETURN 0;
        END IF;
        INSERT INTO client(cookie, lastdate) VALUES (clcookie, NOW());
        SELECT INTO cur curval(client_id_seq);
        INSERT INTO cart(clien_id, stts, startday) VALUES (cur, 'on', NOW());
        IF FOUND THEN
            RETURN 1;
        END IF;
            RETURN 0;
    END;
    $$ LANGUAGE plpgsql;
    */

    @Override
    public boolean initCart(@Nullable String cookie) {
        if (cookie != null) {
            try {
                String sql = "SELECT INIT_CART(?);".intern();
                return jdbcTemplate.update(sql, cookie) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean deleteCart(@Nullable Long id) {
        if (id != null) {
            try {
                String sql = "DELETE FROM cart WHERE id =".intern() + id;
                return jdbcTemplate.update(sql) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean insertProductInCart(@Nullable Long cartId, @Nullable Long productId) {
        if (cartId != null && productId != null) {
            try {
                String sql = "INSERT INTO cart_orders(cart_id, product_id) VALUES (?, ?)".intern();
                return jdbcTemplate.update(sql, cartId, productId) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean deleteProductFromCart(@Nullable Long cartId, @Nullable Long productId) {
        if (cartId != null && productId != null) {
            try {
                String sql = "DELETE FROM cart_orders WHERE cart_id =" + cartId + " AND product_id =" + productId; 
                return jdbcTemplate.update(sql) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
