package alex.home.angular.dao;

import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {

    Product selectProduct(Integer id);

    Product selectProductCategoriesComments(Integer id);
    
    List<Product> selectRecommended(Integer limit);
    
    List<Long> selectIds(String query);
    
    List<Product> searchFormSelection(String query);
    
    List<Product> singleStrArgListProdRet(String query);
    
    Integer getProductCount(String query);
    
    Boolean incrementProductMark(Integer id);
    
    Boolean updateProductCategories(Integer id, Integer oldCategoryId, Integer newCategoryId);
    
    Boolean isAllProductsExist(List<Integer> prodIds);
    
    <T, E> void updateSingleField(T cond, E val, String query, String exName, String exMessage);
    
    void insertProduct(InsertProdDto dto);
    
    void deleteProduct(Integer id);
    
    void updateRecommend(String query);
 
}
