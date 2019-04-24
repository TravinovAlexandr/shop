package alex.home.shop.service;

import alex.home.shop.dao.ReportDao;
import alex.home.shop.dto.ClientInfoProductsSum;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService implements ReportDao {
    
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<ClientInfoProductsSum> selectConfirmationReport(Integer cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException();
        }

        try {
            List<ClientInfoProductsSum> infoProductsSums = jdbcTemplate.query("WITH UP AS (UPDATE cart SET lastaccess = NOW() WHERE ID = " + cartId + ") "
                    + "RES AS (SELECT p.name, p.description, p.price, c.name as clname, c.email, c.address, c.telephone, c.client_wish FROM cart c "
                    + "JOIN cart_products co ON c.id = co.cart_id JOIN product p ON co.product_id = p.id  AND c.id = " + cartId + ")"
                    + " SELECT name, description, price, clname, email, address, telephone, client_wish,  (SELECT SUM (price) FROM RES) AS pricesum FROM RES", 
                    (ResultSet rs, int i) -> new ClientInfoProductsSum(rs.getFloat("pricesum"), rs.getString("name"), rs.getString("description"), rs.getFloat("price"), 
                            rs.getString("clname"), rs.getString("email"), rs.getString("address"), rs.getString("telephone"), rs.getString("client_wish")));
            return infoProductsSums;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
}
