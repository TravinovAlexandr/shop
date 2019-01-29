package alex.home.angular.service;

import alex.home.angular.dao.CommentDao;
import alex.home.angular.exception.AdminException;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class CommentService implements CommentDao {
    
    private JdbcTemplate jdbcTemplate;

    @Override @NotNull
    public void deleteComment(@NotNull Long id) {
        if (id == null) {
            throw new AdminException().addMessage("@NotNull Long id == NULL. Ошибка валидации на уровне контроллера.")
                    .addExceptionName("IllegalArgumentException");
        }
        
        try {
            jdbcTemplate.update("DELETE FROM comment WHERE id = " + id);
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
}
