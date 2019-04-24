package alex.home.shop.dao;

import alex.home.shop.domain.Comment;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentDao {
    
    void deleteComment(Integer id);
    
    void addComment(Comment comment);
    
    void updateComment(Comment comment);
    
    List<Comment> selectAllComments(Integer prodId);
}
