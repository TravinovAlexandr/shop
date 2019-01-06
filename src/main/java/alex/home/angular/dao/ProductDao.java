package alex.home.angular.dao;

import alex.home.angular.domain.Product;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {

    Set<Product> selectProductsByCompany(String companyName, int page);
    
    Set<Product> selectProductsByConcreteType(String type, int page);
    
    void insertProduct(Integer quantity, Double price, String name, String desc, String concreteType, Set<String> commonType);
    
    void updateProduct(Integer quantity, Double price, String name, String desc, String concreteType, Set<String> commonType);
}
