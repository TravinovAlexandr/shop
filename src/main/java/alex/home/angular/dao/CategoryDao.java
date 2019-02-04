package alex.home.angular.dao;

import alex.home.angular.domain.Category;
import alex.home.angular.dto.ProductCategoriesUpdate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao {
    
    void updateProductCategories(ProductCategoriesUpdate pcu);
    
    void insertCategory(String name, String description);
    
    void deleteCategory(Long id);

    void upadateCategoryName(Long id , String newName);
    
    void updateCategoryDesc(Long id ,String desc);
    
    void updateCategory(Category category);
    
    Category selectCategory(Long id);
    
    List<Category> selectAllCategories();
    
}
