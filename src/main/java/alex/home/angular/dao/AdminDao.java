package alex.home.angular.dao;

import alex.home.angular.domain.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDao {
    
    Admin selectAdminByNick(String nick);
    
    boolean isAdminNickExist(String nick);
    
    boolean insertAdmin(String nick, String password, int status);
    
    boolean updateNick(String oldNick, String newNick,  String password);
    
    boolean updatePassword(String nick, String newPassword, String oldPassword);
    
    boolean updateStatus(String nick, String password , int status);
    
//    boolean grantDmlPreferences(String nick);
}