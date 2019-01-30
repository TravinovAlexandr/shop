package alex.home.angular.dao;

import alex.home.angular.domain.Category;
import alex.home.angular.dto.ProductCategoriesUpdate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao {
    
    void updateProductCategories(ProductCategoriesUpdate pcu);
    
    boolean insertCategory(String name, String description);
    
    boolean deleteCategory(Long id);

    boolean upadateCategoryName(Long id , String newName);
    
    boolean updateCategoryDesc(Long id ,String desc);
    
    Category selectCategory(Long id);
    
    List<Category> selectAllCategories();
    
}
