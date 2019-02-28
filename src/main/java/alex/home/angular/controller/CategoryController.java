package alex.home.angular.controller;

import alex.home.angular.dao.CategoryDao;
import alex.home.angular.domain.Category;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.exception.AdminException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
    
    private CategoryDao categoryDao;
    private HttpServletResponse hsr;
    
    @PostMapping("/getAllCategories")
    public ResponseRsWrapper getAllCtegories(ResponseRsWrapper rrw) {
        try {
            return rrw.addResponse(categoryDao.selectAllCategories());
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/addCategory")
    public ResponseRsWrapper addCategory(@RequestBody Category category, ResponseRsWrapper rrw) {
        if (category == null || category.name == null || category.description == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
                categoryDao.insertCategory(category.name, category.description);
                return null;
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/updateCategory")
    public ResponseRsWrapper updateCategory(@RequestBody Category category, ResponseRsWrapper rrw) {
        if (category == null || category.name == null || category.description == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            categoryDao.updateCategory(category);
            return null;
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/deleteCategory/{catId}")
    public ResponseRsWrapper deleteCategory(@PathVariable Integer catId, ResponseRsWrapper rrw) {
        if (catId == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            categoryDao.deleteCategory(catId);
            return null;
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @Autowired
    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Autowired
    public void setHttpServletResponse(HttpServletResponse hsr) {
        this.hsr = hsr;
    }
    
}
