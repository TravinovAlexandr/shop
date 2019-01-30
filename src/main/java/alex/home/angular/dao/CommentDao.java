package alex.home.angular.dao;

import alex.home.angular.domain.Comment;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentDao {
    
    void deleteComment(Long id);
    
    void addComment(Comment comment);
    
    List<Comment> selectAllComments(Long prodId);
    
    void updateCommentByAdmin(Comment comment);
}
