package alex.home.angular.dao;

import alex.home.angular.domain.Category;
import alex.home.angular.dto.ProductCategoriesUpdate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao {
    
    void updateProductCategories(ProductCategoriesUpdate pcu);
    
    void insertCategory(String name, String description);
    
    void deleteCategory(Integer id);

    void upadateCategoryName(Integer id , String newName);
    
    void updateCategoryDesc(Integer id ,String desc);
    
    void updateCategory(Category category);
    
    Category selectCategory(Integer id);
    
    List<Category> selectAllCategories();
    
}
