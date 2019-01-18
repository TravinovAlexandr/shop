package alex.home.angular.controller;

import alex.home.angular.dao.CategoryDao;
import alex.home.angular.dto.ResponseRsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CategoryController {
    
    private CategoryDao categoryDao;
    
    @PostMapping("/getAllCategories")
    @ResponseBody public ResponseRsWrapper getAllCtegories(ResponseRsWrapper rrw) {
        return rrw.addResponse(categoryDao.selectAllCategories());
    }

    @Autowired
    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }
    
    
}
