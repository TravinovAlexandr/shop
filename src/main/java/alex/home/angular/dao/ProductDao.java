package alex.home.angular.dao;

import alex.home.angular.domain.Product;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {

    Product selectProduct(String name);
    
    Product selectProductCategory(String name);
    
    Product selectProductCategoryComments(String name);
    
    List<Product> selectProductsLimitOffsetPagination(String categoryName, int limit, int offset);
    
    boolean doSoldActions(String name, int quant, int buyStat, Date lastBuy);
    
    boolean updateProductName(String oldName, String newName);
    
    boolean updateProductDesc(String name, String desc);
    
    boolean updateProductCategory(String name, String categoryName);
    
    boolean updateProductPrice(String name, double price);
    
    boolean updateProductQuant(String name, int quant);
    
    boolean updateProductMark(String name, int mark);
    
    boolean insertProduct(String name);
    
    boolean insertProduct(String name, String desc);
    
    boolean insertProduct(String name, String desc, int price, int quantity);
   
}
