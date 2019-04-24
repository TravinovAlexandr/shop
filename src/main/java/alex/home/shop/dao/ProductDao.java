package alex.home.shop.dao;

import alex.home.shop.domain.Product;
import alex.home.shop.dto.InsertProdDto;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {
    
    Integer getProductCount(String query);
    
    Product selectProductImgsCommentsTags(Integer id);

    Product selectProductCategoriesImgsCommentsTags(Integer id);
    
    List<Product> selectMinProducts(String query);
    
    List<Product> selectListProducts(String query);
    
    List<Product> selectRecommended(Integer limit);
    
    List<Long> selectIds(String query);
    
    List<Product> searchFormSelection(String query);
    
    List<Product> singleStrArgListProdRet(String query);
    
    boolean updateProductCategories(Integer id, Integer oldCategoryId, Integer newCategoryId);
    
    boolean incrementProductMark(Integer id);
    
    boolean isProdNameExist(String prodName);
    
    void insertProduct(InsertProdDto dto);
    
    void deleteProduct(Integer id);
    
    void updateRecommend(String query);
    
    <T, E> void updateSingleField(T cond, E val, String query, String exName, String exMessage);
}
