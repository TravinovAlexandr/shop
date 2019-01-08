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
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AdminService implements AdminDao {
    
    private JdbcTemplate jdbcTemplate;
    private PasswordEncoder passwordEncoder;
    
    @Override
    public boolean isAdminNickExist(@Nullable String nick) {
        if (nick != null) {
            try {
                final String sql = "SELECT(EXISTS (SELECT * FROM admin WHERE nick = ?))::int;".intern();
                return jdbcTemplate.queryForObject(sql, new Object[] {sql} ,Integer.class) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
 
    @Override
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
    public boolean updateNick(String oldNick, String newNick) {
        if (newNick != null && !newNick.equals(oldNick) && isAdminNickExist(oldNick)) {
            try {
                final String sql = "UPDATE TABLE admin SET nick = ? WHERE nick =?".intern();
                return jdbcTemplate.update(sql, newNick, oldNick) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean updatePassword(String nick, String password) {
        if (nick != null && password != null) {
            try {
                final String sql = "UPDATE TABLE admin SET password = ? WHERE nick = ? ".intern();
                return jdbcTemplate.update(sql, passwordEncoder.encode(password), nick) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    } 
    
    @Override
    public boolean deleteAdmin(@Nullable String nick) {
        if (nick != null) {
            try {
                final String sql = "DELETE FROM admin WHERE nick = ?".intern();
                return jdbcTemplate.update(sql, nick) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        } 
        return false;
    }

    @Override
    public boolean updateStatus(String nick, int status) {
        if (isAdminNickExist(nick)) {
            try {
                final String sql = "UPDATE TABLE admin SET status = ? WHERE nick = ?".intern();
                return jdbcTemplate.update(sql, status, nick) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
        
    @Override
    public Admin selectAdmin(@Nullable String nick) {
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
    
    @Override
    public List<Admin> selectAllAdmins() {
        try {
          final String sql = "SELECT * FROM admin".intern();
          final List<Admin> admins = jdbcTemplate.query(sql, (ResultSet rs, int i) -> new Admin(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDate(4)));
          return admins;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
