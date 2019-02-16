package alex.home.angular.service;

import alex.home.angular.dao.CommentDao;
import alex.home.angular.domain.Comment;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import alex.home.angular.utils.DateUtil;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService implements CommentDao {
    
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteComment(@NotNull Long id) {
        if (id == null) {
            throw new AdminException().addMessage("Controller validation args error.").addExceptionName("IllegalArgumentException");
        }
        
        try {
            jdbcTemplate.update("DELETE FROM " + PGMeta.COMMENT_TABLE + " WHERE id = " + id);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void addComment(@NotNull Comment comment) {
        if (comment == null || comment.productId == null ||  comment.nick == null || comment.body == null) {
            throw new AdminException().addMessage("Controller validation args error.").addExceptionName("IllegalArgumentException");
        }
        
        try {
            jdbcTemplate.update("INSERT INTO " + PGMeta.COMMENT_TABLE + " (product_id, nick , body, date) VALUES (?, ?, ?, ?);", 
                    comment.productId, comment.nick, comment.body, DateUtil.getCurrentTimestamp());
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @NotNull
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Comment> selectAllComments(@NotNull Long prodId) {
        if (prodId == null) {
            throw new AdminException().addMessage("Controller validation args error.").addExceptionName("IllegalArgumentException");
        }
        
        try {
            return jdbcTemplate.query("SELECT * FROM " + PGMeta.COMMENT_TABLE + " WHERE product_id = " + prodId, (ResultSet rs, int i) 
                    -> new Comment(rs.getLong("id"), rs.getString("body"), rs.getString("nick")));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void updateComment(@NotNull Comment comment) {
        if (comment == null || comment.id == null || comment.nick == null || comment.body == null) {
            throw new AdminException().addMessage("Controller validation args error.").addExceptionName("IllegalArgumentException");
        }
        
        try {
            jdbcTemplate.update("UPDATE " + PGMeta.COMMENT_TABLE + " SET nick = ?, body = ? WHERE id = " + comment.id, comment.nick, comment.body);
        } catch(DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
}
