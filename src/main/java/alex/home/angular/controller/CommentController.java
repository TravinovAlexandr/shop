package alex.home.angular.controller;

import alex.home.angular.dao.CommentDao;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.exception.AdminException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public class CommentController {

    private CommentDao commentDao;
    private HttpServletResponse resp;
    
    @PostMapping("/admin/deleteComment/{id}")
    public ResponseRsWrapper deleteComment(@PathVariable Long id, ResponseRsWrapper rrw) {
        if (id != null) {
            try {
            commentDao.deleteComment(id);
            } catch (AdminException ex) {
                return rrw.addResponse(ex.get()).addHttpErrorStatus(resp, 500);
            }
        }
        return null;
    }
    
    
    
    @Autowired
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Autowired
    public void setResp(HttpServletResponse resp) {
        this.resp = resp;
    }
    
    
    
    
}
