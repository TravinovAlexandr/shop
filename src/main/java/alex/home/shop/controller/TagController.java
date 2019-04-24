package alex.home.shop.controller;

import alex.home.shop.dao.TagDao;
import alex.home.shop.dto.ResponseRsWrapper;
import alex.home.shop.dto.TagCount;
import alex.home.shop.exception.AdminException;
import alex.home.shop.utils.ValidationUtil;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TagController {
    
    private TagDao tagDao;
    private HttpServletResponse hsr;
    
    @PostMapping("/admin/getTagsLike/{word}/{limit}/{offset}")
    public ResponseRsWrapper getTagsLike(@PathVariable(required = false) String word, @PathVariable(required = false) Integer limit, 
            @PathVariable(required = false) Integer offset, ResponseRsWrapper rrw) {
        if (!ValidationUtil.validateNull(limit, offset) || word == null || word.trim().equals("")) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            return rrw.addResponse(tagDao.selectTagsLike(word, limit, offset));
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/getOrphanedTags/{limit}/{offset}")
    public ResponseRsWrapper getOrphanedTags(@PathVariable(required = false) String limit, @PathVariable(required = false) String offset, ResponseRsWrapper rrw) {
        if (!ValidationUtil.validateNull(limit, offset)) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            return rrw.addResponse(tagDao.selectOrphanedTags(Integer.parseInt(limit), Integer.parseInt(offset)));
        } catch (NumberFormatException | AdminException ex) {
            ex.printStackTrace();
            if (ex.getClass() == AdminException.class) {
                return rrw.addResponse( ((AdminException) ex).get()).addHttpErrorStatus(hsr, 500);  
            }
            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 500);  
        }
    }
    
    @PostMapping("/admin/selectOrphanedTags/")
    public ResponseRsWrapper updateTag(ResponseRsWrapper rrw) {
        try {
            tagDao.removeOrphanedTags();
            return null;
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(( (AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/addTagGetId/{name}")
    public ResponseRsWrapper addTag(@PathVariable(required = false) String name, ResponseRsWrapper rrw) {
        if (name == null || name.trim().equals("")) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            return rrw.addResponse(tagDao.insertTagGetId(name));
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/updateTag/{id}/{name}")
    public ResponseRsWrapper updateTag(@PathVariable(required = false) String id, @PathVariable(required = false) String name, ResponseRsWrapper rrw) {
        if (id == null || name == null || name.trim().equals("")) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            tagDao.updateTag(Integer.parseInt(id), name);
            return null;
        } catch (NumberFormatException | AdminException ex) {
            ex.printStackTrace();
            if (ex.getClass() == AdminException.class) {
                return rrw.addResponse(( (AdminException) ex).get()).addHttpErrorStatus(hsr, 500);    
            }
            
            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 500);    
        }
    }
    
    @PostMapping("/admin/deleteTag/{id}")
    public ResponseRsWrapper updateTag(@PathVariable(required = false) String id, ResponseRsWrapper rrw) {
        if (id == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            tagDao.removeTagWhereId(Integer.parseInt(id));
            return null;
        } catch (NumberFormatException | AdminException ex) {
            ex.printStackTrace();
            if (ex.getClass() == AdminException.class) {
                return rrw.addResponse(( (AdminException) ex).get()).addHttpErrorStatus(hsr, 500);    
            }
            
            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 500);    
        }
    }
    
    @PostMapping("/admin/removeAllOrphanedTags")
    public ResponseRsWrapper removeAllOrphanedTags(ResponseRsWrapper rrw) {
        try {
            tagDao.removeOrphanedTags();
            return null;
        } catch (AdminException ex) {
            ex.printStackTrace();
            return rrw.addResponse(( (AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
        }
    }

    @Autowired
    public void setHsr(HttpServletResponse hsr) {
        this.hsr = hsr;
    }

    @Autowired
    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }
    
}
