package alex.home.angular.dao;

import alex.home.angular.dto.ClientInfoProductsSum;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDao {
    
    List<ClientInfoProductsSum> selectConfirmationReport(Integer cartId);
}
