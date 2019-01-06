package alex.home.angular.service;

import alex.home.angular.domain.Admin;
import java.sql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import alex.home.angular.dao.AdminDao;
import alex.home.angular.utils.DateUtil;
import javax.annotation.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;

//create table admin (nick varchar(30) not null, password varchar(150) not null, role int not null, time_s timestamp);
//create index on admin(nick);

@Service
public class AdminService implements AdminDao {
    
    private JdbcTemplate jdbcTemplate;
    private PasswordEncoder passwordEncoder;
    
    @Override @Transactional(readOnly = true)
    public Admin selectAdminByNick(@Nullable String nick) {
        if (nick != null) {
            try {
                final String sql = "SELECT * FROM admin WHERE nick = ?".intern();
                final Admin admin = jdbcTemplate.queryForObject(sql, new Object [] {nick}, (ResultSet rs, int i) 
                        -> new Admin(rs.getString("nick"), String.valueOf(rs.getArray("password")), rs.getInt("role"), rs.getTimestamp("time_s")));
                return admin;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    @Override @Transactional(readOnly = true)
    public boolean isAdminNickExist(@Nullable String nick) {
        if (nick != null) {
            try {
                final String sql = "SELECT(EXISTS (SELECT 1 FROM admin WHERE nick = ?))::int;".intern();
                return jdbcTemplate.queryForObject(sql, new Object[] {sql} ,Integer.class) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
 
    @Override @Transactional
    public boolean insertAdmin(String nick, String password, int status) {
        if (nick != null && password != null && !isAdminNickExist(nick)) {
            try {
                final String sql = "INSERT INTO admin (nick, password, role, time_s) VALUES (?, ?, ?, ?)".intern();
                return jdbcTemplate.update(sql, nick,  passwordEncoder.encode(password), status, DateUtil.getCurrentTimestamp()) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    @Override @Transactional
    public boolean updateNick(String oldNick, String newNick,  String password) {
        if (newNick != null && password != null && !newNick.equals(oldNick) && isAdminNickExist(oldNick)) {
            try {
                final String sql = "UPDATE TABLE admin SET nick = ? WHERE nick =? AND password = ?".intern();
                return jdbcTemplate.update(sql, newNick, oldNick , password.toCharArray()) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override @Transactional
    public boolean updatePassword(String nick, String newPassword, String oldPassword) {
        if (newPassword != null && oldPassword != null) {
            try {
                final String sql = "UPDATE TABLE admin SET password = ? WHERE nick = ? AND password = ?".intern();
                return jdbcTemplate.update(sql, newPassword.toCharArray(), nick, oldPassword.toCharArray()) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    } 

    @Override @Transactional
    public boolean updateStatus(String nick, String password , int status) {
        if (password != null  && isAdminNickExist(nick)) {
            try {
                final String sql = "UPDATE TABLE admin SET status = ? WHERE nick = ? AND password = ?".intern();
                return jdbcTemplate.update(sql, status, nick, password.toCharArray()) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
//    @Override @Transactional
//    public boolean grantDmlPreferences(String nick) {
//        if (nick != null && isAdminExist(nick)) {
//            try {
//                final String sql = "GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO ?;"
//                        + "GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO alex; ".intern();
//                int i  = jdbcTemplate.update(sql, nick);
//                System.out.println(i);
//                System.out.println(i);
//                System.out.println(i);
//                return true;
//            } catch (DataAccessException ex) {
//                ex.printStackTrace();
//                return false;
//            }
//        }
//        return false;
//    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    } 
}
