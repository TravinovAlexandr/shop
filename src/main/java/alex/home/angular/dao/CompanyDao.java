package alex.home.angular.dao;

import alex.home.angular.domain.Company;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDao {
    
    boolean isCompanyNameExist(String name);
    
    boolean insertCompany(String name, String desc, String address);
    
    boolean deleteCompanyByName(String name);
    
    boolean updateCompanyName(String oldName, String newName);
    
    boolean updateCompanyDesc(String name, String desc);
    
    boolean updateCompanyAddress(String name, String address);
    
    int selectCompanyCount();
    
    Company selectCompanyByName(String name);
    
    List<Company> selectLimitOffsetPagination(int limit, int offset);
    
}
