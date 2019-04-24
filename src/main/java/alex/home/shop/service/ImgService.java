package alex.home.shop.service;

import alex.home.shop.annotation.NotNullArgs;
import alex.home.shop.dao.ImgDao;
import alex.home.shop.domain.Img;
import alex.home.shop.exception.AdminException;
import alex.home.shop.sql.PGMeta;
import java.sql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImgService implements ImgDao {

    private JdbcTemplate jdbcTemplate;

    @Override @NotNullArgs
    public void updateImg(Integer imgId, String url) {
        if (imgId == null || url == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException");
        }
        
        try {
            jdbcTemplate.update("UPDATE " + PGMeta.IMG_TABLE + " SET url = ?  WHERE id = " + imgId, url);
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
