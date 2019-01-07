package alex.home.angular.dao;

import alex.home.angular.domain.Admin;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDao {
    
    boolean isAdminNickExist(String nick);
    
    boolean insertAdmin(String nick, String password, int status);
    
    boolean updateNick(String oldNick, String newNick);
    
    boolean updatePassword(String nick, String password);
    
    boolean updateStatus(String nick,  int status);
    
    boolean deleteAdmin(String nick);
    
    Admin selectAdminByNick(String nick);
    
    List<Admin> selectAllAdmins();
}