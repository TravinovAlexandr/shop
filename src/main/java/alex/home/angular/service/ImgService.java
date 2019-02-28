package alex.home.angular.service;

import alex.home.angular.annotation.NotNullArgs;
import alex.home.angular.dao.ImgDao;
import alex.home.angular.domain.Img;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import java.sql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImgService implements ImgDao {

    private JdbcTemplate jdbcTemplate;

    @Override @NotNullArgs
    public Img selectImgById(Integer imgId) {
        if (imgId == null) {
            throw new AdminException().addMessage("IllegalArgumentException");
        }
        
        try {
            return jdbcTemplate.queryForObject("SELECT id, url FROM " + PGMeta.IMG_TABLE + " WHERE id = " + imgId, (ResultSet rs, int i) 
                    -> { return new Img(rs.getInt("id"), rs.getString("url"));});
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

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
