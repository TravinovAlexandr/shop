package alex.home.angular.controller;

import alex.home.angular.dao.CategoryDao;
import alex.home.angular.dto.ProductCategoriesUpdate;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.exception.AdminException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductUpdateController {
    
    private CategoryDao categoryDao;
    private HttpServletResponse hsr;
    
    @PostMapping("/admin/updateCategories")
    public ResponseRsWrapper updateCategories(@RequestBody ProductCategoriesUpdate pcu, ResponseRsWrapper rrw) {
        if (pcu == null || pcu.productId == null || pcu.oldCategoriesId == null || pcu.newCategoriesId == null 
                || (pcu.oldCategoriesId.isEmpty() && pcu.newCategoriesId.isEmpty())) {
            
            return rrw.addResponse(new AdminException().addMessage("@RequestBody ProductCategoriesUpdate pcu == null || pcu.productId == null || pcu.categoriesId == null")
                    .addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            int oldCategLength = pcu.oldCategoriesId.size();
            int newCategLength = pcu.newCategoriesId.size();
            List<Long> categoreToDelete = new ArrayList<>();
            List<Long> categoreToInsert = new ArrayList<>();
            
            if (oldCategLength == 0 && newCategLength != 0) {
                categoreToInsert.addAll(pcu.newCategoriesId);
            } else if (newCategLength == 0 && oldCategLength != 0) {
                 categoreToDelete.addAll(pcu.oldCategoriesId);
            } else {
                List<Long> tmp = new ArrayList<>(pcu.oldCategoriesId);
                tmp.removeAll(pcu.newCategoriesId);
                categoreToDelete.addAll(tmp);
                
                tmp = new ArrayList<>(pcu.newCategoriesId);
                tmp.removeAll(pcu.oldCategoriesId);
                categoreToInsert.addAll(tmp);
            }
            
            pcu.oldCategoriesId = categoreToDelete;
            pcu.newCategoriesId = categoreToInsert;

            categoryDao.updateProductCategories(pcu);
            
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
