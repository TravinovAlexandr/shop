package alex.home.angular.service;

import alex.home.angular.dao.CartDao;;
import alex.home.angular.exception.AdminException;
import alex.home.angular.utils.DateUtil;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartService implements CartDao {
    
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public String createCartGetCookieUUID() {
        try {
            UUID cartUUID = UUID.randomUUID();
            jdbcTemplate.update("INSERT INTO cart (cookie, startday, stts) VALUES (?,?,"+ "'on'" +");", cartUUID, DateUtil.getCurrentTimestamp());
            return cartUUID.toString();
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
