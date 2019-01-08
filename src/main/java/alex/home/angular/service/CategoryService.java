package alex.home.angular.service;

import alex.home.angular.dao.CategoryDao;
import alex.home.angular.domain.Category;
import java.sql.ResultSet;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService implements CategoryDao {
    
    private JdbcTemplate jdbcTemplate;

    @Override 
    public boolean insertCategory(@Nullable String name, @Nullable String description) {
        if (name != null && description != null) {
            try {
                final String sql = "SELECT INSERT_CATEGORY(?, ?)".intern();
                return jdbcTemplate.query(sql, new Object[] {name, description}, (ResultSet rs, int i) -> rs.getInt(1)).get(1) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override @Transactional
    public boolean deleteCategory(@Nullable String name) {
        if (name != null) {
            try {
                final String sql = "DELETE FROM category WHERE name = ?".intern();
                return jdbcTemplate.update(sql, name) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        } 
        return false;
    }
    
    @Override
    public boolean upadateCategoryName(@Nullable String oldName, @Nullable String newName) {
        if (oldName != null && newName != null) {
            try {
                final String sql = "UPDATE_CATEGORY_NAME(?, ?);".intern();
                return jdbcTemplate.update(sql, oldName, newName) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean updateCategoryDesc(@Nullable String name, @Nullable String desc) {
        if (name != null && desc != null) {
                try {
                    final String sql = "UPDATE category SET description = desc WHERE name = name;".intern();
                    return jdbcTemplate.update(sql, name, desc) == 1;
                } catch (DataAccessException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
            return false;
    }

    @Override
    public Category selectCategory(@Nullable String name) {
        if (name != null) {
            try {
                final String sql = "SELECT * FROM category WHERE name = ? LIMIT 1;".intern();
                final Category category = jdbcTemplate.queryForObject(sql, new Object [] {name}, (ResultSet rs, int i) 
                        -> new Category(rs.getLong("id"), rs.getString("name"), rs.getString("description")));
                return category;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public List<Category> selectAllCategory() {
        try {
          final String sql = "SELECT * FROM category;".intern();
          final List<Category> categories = jdbcTemplate.query(sql, (ResultSet rs, int i) -> new Category(rs.getLong("id"), rs.getString("name"), rs.getString("description")));
          return categories;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
}
