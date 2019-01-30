package alex.home.angular.controller;

import alex.home.angular.dao.CommentDao;
import alex.home.angular.domain.Comment;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.exception.AdminException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentController {

    private CommentDao commentDao;
    private HttpServletResponse hsr;

    @PostMapping("/admin/addComment")
    @ResponseBody public ResponseRsWrapper addComment(@RequestBody Comment comment, ResponseRsWrapper rrw) {
        if (comment == null || comment.productId == null || comment.nick == null || comment.body == null) {
            return rrw.addHttpErrorStatus(hsr, 500).addResponse(new AdminException().addExceptionName("IllegalAttributeException")
                    .addMessage("@RequestBody Comment comment == null || comment.productId == null ||  comment.nick == null || comment.body == null"));
        }
        
        try {
            commentDao.addComment(comment);
            return rrw;
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/deleteComment/{id}")
    @ResponseBody public ResponseRsWrapper deleteComment(@PathVariable Long id, ResponseRsWrapper rrw) {
        if (id != null) {
            try {
            commentDao.deleteComment(id);
            } catch (AdminException ex) {
                ex.printStackTrace();
                return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
            }
        }
        return rrw;
    }
    
    @PostMapping("/admin/getAllProductComments/{prodId}")
    @ResponseBody public ResponseRsWrapper getAllProductComments(@PathVariable Long prodId, ResponseRsWrapper rrw) {
        if (prodId == null) {
            return rrw.addHttpErrorStatus(hsr, 500).addResponse(new AdminException().addExceptionName("IllegalAttributeException")
                    .addMessage("/admin/getAllProductComments/{prodId} @PathVariable Long id == null"));
        }
        
        try {
            return rrw.addResponse(commentDao.selectAllComments(prodId));
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addHttpErrorStatus(hsr, 500).addResponse(ex.get());
        }
    }
    
    @PostMapping("/admin/updateComment")
    @ResponseBody public ResponseRsWrapper updateComment(@RequestBody Comment comment, ResponseRsWrapper rrw) {
        if (comment == null || comment.id == null || comment.nick == null || comment.body == null) {
            return rrw.addHttpErrorStatus(hsr, 500).addResponse(new AdminException().addExceptionName("IllegalAttributeException")
                    .addMessage("/admin/updateComment @RequestBody Comment comment== null || comment.id == null "
                            + "|| comment.nick == null || comment.body == null"));
        }
        
        try {
            commentDao.updateCommentByAdmin(comment);
            return null;
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addHttpErrorStatus(hsr, 500).addResponse(ex.get());
        }
    }
    
    @Autowired
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Autowired
    public void setResp(HttpServletResponse hsr) {
        this.hsr = hsr;
    }
    
    
    
    
}
