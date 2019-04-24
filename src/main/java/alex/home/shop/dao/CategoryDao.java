package alex.home.shop.dao;

import alex.home.shop.domain.Category;
import alex.home.shop.dto.ProductCategoriesUpdate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao {
    
    void updateProductCategories(ProductCategoriesUpdate pcu);

    void upadateCategoryName(Integer id , String newName);
    
    void updateCategoryDesc(Integer id ,String desc);
        
    boolean deleteCategory(Integer id);
    
    boolean updateCategory(Category category);
    
    boolean isCategoryNameExist(Integer catId, String catName);
    
    boolean isCategoryPidExist(String catPid);
    
    Integer insertCategory(Integer pid, String name, String description, Boolean leaf);
    
    Category selectCategory(Integer id);
    
    List<Category> selectAllCategories();
    
    List<Category> selectLeafCategories();
    
}
