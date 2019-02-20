package alex.home.angular.transaction;

import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductsCount;
import alex.home.angular.dto.SubmitContract;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface TransactionFacade {
    
    Product selectProduct(Long id);
    
    Product selectProductCategoriesComments(Long id);
    
    ProductsCount selectProductsWhereCtegoryId(Long categoryId, Integer limit, Integer offset);
    
    ProductsCount searchFormSelection(String query);

    List<Product> selectLastAddedInCategory(Long catId, Integer limit);
    
    List<Product> selectLastAddedInAllCategories(Integer limit);
    
    List<Product> selectRecommend(Integer limit);
    
    List<Product> checkCartProducts(SubmitContract sc);
    
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
    
    void updateRecommend(Long prodId);
    
    void submitContract(SubmitContract sc);
    
    void addProductInCart(Long cartId, Long prodId);

    void deleteProductFromCart(Long cartId, Long prodId);

    void decProductInCart(Long cartId, Long prodId);

    void incProductInCart(Long cartId, Long prodId);
}
