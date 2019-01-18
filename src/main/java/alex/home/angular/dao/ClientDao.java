package alex.home.angular.dao;

import alex.home.angular.domain.Client;
import java.util.Date;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDao {
    
    Client selectClient(Long id);
    
    Client selectClient(String coockie);
    
    boolean updateClientBeforeAgreement(String cookie, String name, Byte age, String email, String mobilePhone, String homePhone, String address);
    
    boolean updateLastDate(String cookie, Date lastDate);
    
    boolean updateClientInfo(Long id, String info);
    
    boolean deleteClient(Long id);
}
