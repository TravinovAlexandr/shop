package alex.home.shop.controller;

import alex.home.shop.dao.CategoryDao;
import alex.home.shop.domain.Category;
import alex.home.shop.dto.ResponseRsWrapper;
import alex.home.shop.dto.CategoryTree;
import alex.home.shop.exception.AdminException;
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
    
    @PostMapping("/isCategoryPidExist/{catPid}")
    public ResponseRsWrapper isCategoryPidExist(@PathVariable String catPid, ResponseRsWrapper rrw) {
        if (catPid == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        if (catPid.equals("0")) {
            return rrw.addResponse(false);
        }
        
        try {
            return rrw.addResponse(categoryDao.isCategoryPidExist(catPid));
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/isCategoryNameExist/{catId}/{catName}")
    public ResponseRsWrapper isCategoryNameExist(@PathVariable Integer catId, @PathVariable String catName, ResponseRsWrapper rrw) {
        if (catName == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            return rrw.addResponse(categoryDao.isCategoryNameExist(catId, catName));
        } catch (AdminException | NumberFormatException ex) {
            ex.printStackTrace();
            if (ex.getClass() == AdminException.class) {
                return rrw.addResponse( ((AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
            }
            
            return rrw.addResponse(ex).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/getAllCategoriesList")
    public ResponseRsWrapper getAllCtegoriesList(ResponseRsWrapper rrw) {
        try {
            return rrw.addResponse(categoryDao.selectAllCategories());
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/getAllCategories")
    public ResponseRsWrapper getAllCtegoriesTree(ResponseRsWrapper rrw) {
        try {
            return rrw.addResponse(CategoryTree.createTree(categoryDao.selectAllCategories()));
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/addCategory")
    public ResponseRsWrapper addCategory(@RequestBody Category category, ResponseRsWrapper rrw) {
        if (category == null || category.pid == null ||  category.isLeaf == null  || category.name == null || category.description == null ) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
                return rrw.addResponse(categoryDao.insertCategory(category.pid, category.name, category.description, category.isLeaf));
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/updateCategory")
    public ResponseRsWrapper updateCategory(@RequestBody Category category, ResponseRsWrapper rrw) {
        if (category == null || category.id == null || category.pid == null || category.name == null || category.description == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            if (!categoryDao.updateCategory(category)) {
                return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").addMessage("Not unique category name").get()).addHttpErrorStatus(hsr, 400);
            }
            
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
            if (!categoryDao.deleteCategory(catId)) {
                return rrw.addResponse(new AdminException().addExceptionName("DataIntegrityViolationException").addMessage("Category has child category nodes.").get()).addHttpErrorStatus(hsr, 400);
            }
            
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
