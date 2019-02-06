package alex.home.angular.dao;

import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductRow;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {

    Product selectProduct(Long id);
    
    Product selectProductWithImage(Long id);
    
    Product selectProductCategories(Long id);
    
    Product selectProductCategoriesComments(Long id);
    
    List<Product> selectProductsWhereCtegoryId(Long categoryId, Integer limit, Integer offset);
    
    List<Product> selectProductsWhereCategoryNotExist(Integer limit, Integer offset);
    
    List<Product> selectProductsInSomeTimePeriod(Long mils, Integer limit, Integer offset);
    
    List<ProductRow> searchFormsSelection(String query);
    
    Integer getProductCount(String query);
    
    boolean doSoldActions(Long id, Integer quant, Integer buyStat, Date lastBuy);
    
    void updateProductName(Long id, String newName);
    
    void updateProductDesc(Long id, String desc);
    
    boolean updateProductCategory(Long id, Long oldCategoryId, Long newCategoryId);
    
    void updateProductPrice(Long id, Float price);
    
    void updateProductQuant(Long id, Integer quant);
    
    void updateProductMark(Long id, Integer mark);
    
    void insertProduct(InsertProdDto dto);
    
    void deleteProduct(Long id);
 
}
