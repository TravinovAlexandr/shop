package alex.home.angular.dao;

import alex.home.angular.domain.Comment;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentDao {
    
    void deleteComment(Integer id);
    
    void addComment(Comment comment);
    
    void updateComment(Comment comment);
    
    List<Comment> selectAllComments(Integer prodId);
}
