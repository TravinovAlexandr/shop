package alex.home.angular.dao;

import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductRow;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {

    Product selectProduct(Long id);

    Product selectProductCategoriesComments(Long id);
    
    List<Product> selectProductsWhereCtegoryId(String query);
    
    List<Product> selectLastAdded(String query);
    
    List<Product> selectRecommended(Integer limit);
    
    List<Long> selectIds(String query);
    
    List<ProductRow> searchFormSelection(String query);
    
    Integer getProductCount(String query);
    
    boolean incrementProductMark(Long id);
    
    boolean updateProductCategories(Long id, Long oldCategoryId, Long newCategoryId);
    
    <T, E> void updateSingleField(T cond, E val, String query, String exName, String exMessage);
    
    void insertProduct(InsertProdDto dto);
    
    void deleteProduct(Long id);
 
}
