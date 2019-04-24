package alex.home.shop.dao;

import alex.home.shop.dto.ClientInfoProductsSum;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDao {
    
    List<ClientInfoProductsSum> selectConfirmationReport(Integer cartId);
}
