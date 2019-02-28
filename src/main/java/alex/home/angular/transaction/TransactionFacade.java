package alex.home.angular.transaction;

import alex.home.angular.domain.Product;
import alex.home.angular.dto.ClientInfoProductsSum;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductsCount;
import alex.home.angular.dto.SubmitContract;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface TransactionFacade {
    
    Product selectProduct(Integer id);
    
    Product selectProductCategoriesComments(Integer id);
    
    ProductsCount selectProductsWhereCtegoryId(Integer categoryId, Integer limit, Integer offset);
    
    ProductsCount searchFormSelection(String query);

    List<Product> selectLastAddedInCategory(Integer catId, Integer limit);
    
    List<Product> selectLastAddedInAllCategories(Integer limit);
    
    List<Product> selectRecommend(Integer limit);
    
    List<Product> checkCartProducts(SubmitContract sc);
    
    List<ClientInfoProductsSum> submitContract(SubmitContract sc);
    
    Integer getProductCount(String query);
    
    Boolean isConfirmed(Integer cartId);
    
    boolean incrementProductMark(Integer id);
    
    boolean updateProductCategories(Integer id, Integer oldCategoryId, Integer newCategoryId);
    
    void updateProductName(Integer id, String newName);
    
    void updateProductDesc(Integer id, String desc);
    
    void updateProductPrice(Integer id, Float price);
    
    void updateProductQuant(Integer id, Integer quant);
    
    void updateProductMark(Integer id, Integer mark);
    
    void insertProduct(InsertProdDto dto);
    
    void deleteProduct(Integer id);
    
    void updateRecommend(Integer prodId);
    
    void addProductInCart(Integer cartId, Integer prodId);

    void deleteProductFromCart(Integer cartId, Integer prodId);

    void decProductInCart(Integer cartId, Integer prodId);

}
