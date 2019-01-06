package alex.home.angular.service;

import alex.home.angular.dao.CompanyDao;
import alex.home.angular.domain.Company;
import java.sql.ResultSet;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//create table company (id bigserial not null primary key, name varchar(50) not null, addres varchar(100) not null, description text);
//create index on company(name);

@Service
public class CompanyService implements CompanyDao {

    private JdbcTemplate jdbcTemplate;
    
    @Override @Transactional(readOnly = true)
    public boolean isCompanyNameExist(String name) {
        if (name != null) {
            try {
                final String sql = "SELECT(EXISTS (SELECT 1 FROM company WHERE name = ?))::int;".intern();
                return jdbcTemplate.queryForObject(sql, new Object[] {name}, Integer.class) == 1;
            } catch(DataAccessException ex) {
              ex.printStackTrace();
              return false;
            }
        }
        return false;
    }
     
    @Override @Transactional
    public boolean insertCompany(String name, String desc, String address) {
        if (desc != null && address != null && !isCompanyNameExist(name)) {
            try {
                final String sql = "INSERT INTO company (name, description, address) VALUES (?, ?, ?)".intern();
                return jdbcTemplate.update(sql, name, desc, address) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
       }
        return false;
    }

    @Override @Transactional
    public boolean deleteCompanyByName(String name) {
        if (isCompanyNameExist(name)) {
            try {
                final String sql = "DELETE FROM company WHERE name = ?".intern();
                return jdbcTemplate.update(sql, name) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override @Transactional
    public boolean updateCompanyName(String oldName, String newName) {
        if (newName != null && isCompanyNameExist(oldName)) {
            try {
                final String sql = "UPDATE TABLE company SET name = ? WHERE name = ?".intern();
                return jdbcTemplate.update(sql, newName, oldName) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    @Override
    public boolean updateCompanyDesc(String name, String desc) {
        if (desc != null && isCompanyNameExist(name)) {
            try {
                final String sql = "UPDATE TABLE company SET description = ? WHERE name = ?".intern();
                return jdbcTemplate.update(sql, name, desc) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    @Override @Transactional
    public boolean updateCompanyAddress(String name, String address) {
        if (address != null && isCompanyNameExist(name)) {
            try {
                final String sql = "UPDATE TABLE company SET address = ? WHERE name = ?".intern();
                return jdbcTemplate.update(sql, address, name) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override @Transactional(readOnly = true)
    public List<Company> selectLimitOffsetPagination(int limit, int offset) {
        try {
            final String sql = "SELECT * FROM company ORDER BY name LIMIT ? OFFSET ?".intern();
            final List<Company> cmpns = (List<Company>) jdbcTemplate.queryForObject(sql, new Object[] {limit, offset * limit}, (ResultSet rs, int i) -> {
                return new Company(rs.getLong("id"), rs.getString("name"), rs.getString("description"), rs.getString("address"));});
            return cmpns;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override @Transactional(readOnly = true)
    public int selectCompanyCount() {
        try {
            final String sql = "SELECT COUNT(id) AS count FROM company".intern();
            return jdbcTemplate.query(sql, (ResultSet rs) -> rs.getInt("count"));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    @Override @Transactional(readOnly = true)
    public Company selectCompanyByName(@Nullable String name) {
        if (name != null) {
            try {
                final String sql = "SELECT * FROM company WHERE name = ?".intern();
                final Company company = jdbcTemplate.queryForObject(sql, new Object[] {name}, (ResultSet rs , int i) -> 
                    new Company(rs.getLong("id"), rs.getString("name"), rs.getString("desc"), rs.getString("address")));
                return company;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
}
