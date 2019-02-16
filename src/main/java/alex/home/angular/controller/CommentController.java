package alex.home.angular.controller;

import alex.home.angular.dao.CommentDao;
import alex.home.angular.domain.Comment;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.exception.AdminException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentController {

    private CommentDao commentDao;
    private HttpServletResponse hsr;

    @PostMapping(value = { "/admin/addComment", "/addComment" })
    @ResponseBody public ResponseRsWrapper addComment(@RequestBody Comment comment, ResponseRsWrapper rrw) {
        if (comment == null || comment.productId == null || comment.nick == null || comment.body == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            commentDao.addComment(comment);
            return null;
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/deleteComment/{id}")
    @ResponseBody
    public ResponseRsWrapper deleteComment(@PathVariable Long id, ResponseRsWrapper rrw) {
        if (id == null) {
            return rrw.addHttpErrorStatus(hsr, 400).addResponse(new AdminException().addExceptionName("IllegalArgumentException").addMessage("Check args names"));
        }

        try {
            commentDao.deleteComment(id);
            return null;
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping(value = { "/admin/getAllProductComments/{prodId}", "/getAllComments/{prodId}"})
    @ResponseBody public ResponseRsWrapper getAllProductComments(@PathVariable Long prodId, ResponseRsWrapper rrw) {
        if (prodId == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            return rrw.addResponse(commentDao.selectAllComments(prodId));
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/updateComment")
    @ResponseBody public ResponseRsWrapper updateComment(@RequestBody Comment comment, ResponseRsWrapper rrw) {
        if (comment == null || comment.id == null || comment.nick == null || comment.body == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            commentDao.updateComment(comment);
            return null;
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
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
