package alex.home.angular.transaction;

import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductRow;
import alex.home.angular.dto.ProductsCount;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface TransactionProduct {
    
    Product selectProduct(Long id);
    
    Product selectProductCategoriesComments(Long id);
    
    ProductsCount selectProductsWhereCtegoryId(Long categoryId, Integer limit, Integer offset);
    
    List<ProductRow> searchFormSelection(String query);

    List<Product> selectLastAddedInCategory(Long catId, Integer limit);
    
    List<Product> selectLastAddedInAllCategories(Integer limit);
    
    List<Product> selectRecommend(Integer limit);
    
    Integer getProductCount(String query);
    
    boolean incrementProductMark(Long id);
    
    boolean updateProductCategories(Long id, Long oldCategoryId, Long newCategoryId);
    
    void updateProductName(Long id, String newName);
    
    void updateProductDesc(Long id, String desc);
    
    void updateProductPrice(Long id, Float price);
    
    void updateProductQuant(Long id, Integer quant);
    
    void updateProductMark(Long id, Integer mark);
    
    void insertProduct(InsertProdDto dto);
    
    void deleteProduct(Long id);
        
}
