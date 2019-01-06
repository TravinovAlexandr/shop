package alex.home.angular.service;

import alex.home.angular.dao.AdminDao;
import alex.home.angular.domain.Admin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService {

    private AdminDao adminDao;
    
    @Override
    public UserDetails loadUserByUsername(String nick) throws UsernameNotFoundException {

        final Admin admin = adminDao.selectAdminByNick(nick);
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(String.valueOf(admin.getRole())));
        
        return new org.springframework.security.core.userdetails.User(
                admin.getNick(),
                admin.getPassword(),
                authorities
        );  
    }

    @Autowired
    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    
}
