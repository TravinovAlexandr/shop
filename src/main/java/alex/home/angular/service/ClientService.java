package alex.home.angular.service;

import alex.home.angular.dao.ClientDao;
import alex.home.angular.domain.Client;
import java.util.Date;
import javax.annotation.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements ClientDao {

    private JdbcTemplate jdbcTemplate;
    
    private Client selectClnt(String sql) {
        if (sql != null) {
            try {
                return jdbcTemplate.query(sql, ((rs) -> {
                    return new Client().addId(rs.getLong("id")).addAge(rs.getByte("age")).addName(rs.getString("name"))
                            .addEmail(rs.getString("email")).addEmail(rs.getString("info")).addMobilePhone(rs.getString("mobile"))
                            .addHomePhone(rs.getString("home")).addAddress(rs.getString("address")).addEmail(rs.getString("email"));
                        }));
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    @Override @Nullable
    public Client selectClient(@Nullable Long id) {
        return selectClnt("SELECT * FROM client WHERE id = ".intern() + id);
    }
    
    
    @Override @Nullable
    public Client selectClient(@Nullable String coockie) {
        return selectClnt("SELECT * FROM client WHERE coockie = ".intern() + coockie);
    }

    @Override
    public boolean updateClientBeforeAgreement(@Nullable String cookie,@Nullable String name,@Nullable Byte age,@Nullable String email,
            @Nullable  String mobilePhone, @Nullable String homePhone, @Nullable String address) {
        if (email == null && mobilePhone == null && homePhone == null) {
            return false;
        }
        try {
        String sql = "UPDATE client SET name =?, age = ?, email = ?, mobile = ?, home = ?, address = ? WHERE cookie = ?;".intern();
        return jdbcTemplate.update(sql, name, age, email, mobilePhone, homePhone, address, cookie) != 0;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateLastDate(@Nullable String cookie,@Nullable Date lastDate) {
        if (cookie != null && lastDate != null) {
            try {
                String sql = "UPDATE client SET date = ? WHERE cookie =".intern() + cookie;
                return jdbcTemplate.update(sql, lastDate) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean updateClientInfo(@Nullable  Long id,@Nullable String info) {
        if (id != null || info != null) {
            try {
                String sql = "UPDATE client SET info = ? WHERE id =".intern() +id;
                return jdbcTemplate.update(sql, info) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean deleteClient(@Nullable Long id) {
        if (id != null) {
            try {
                String sql = "DELETE FROM client WHERE id =".intern() +id;
                return jdbcTemplate.update(sql) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
        
}
