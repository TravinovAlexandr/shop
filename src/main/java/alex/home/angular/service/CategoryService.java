package alex.home.angular.service;

import alex.home.angular.dao.CategoryDao;
import alex.home.angular.domain.Category;
import alex.home.angular.dto.ProductCategoriesUpdate;
import alex.home.angular.exception.AdminException;
import alex.home.angular.utils.ConnectionUtil;
import alex.home.angular.utils.PGUtil;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

@Service
public class CategoryService implements CategoryDao {
    
    private JdbcTemplate jdbcTemplate;
    SimpleJdbcInsert simpleJdbcInsert;    
    /*
    CREATE OR REPLACE FUNCTION UPDATE_PRODUCT_CATEGORIES(prodid BIGINT, catsToDelete BIGINT[], catsToInsert BIGINT[])
    RETURNS VOID AS $$
    DECLARE
        dltid BIGINT;
        insrtid BIGINT;
    BEGIN
        IF prodid IS NOT NULL AND catsToDelete IS NOT NULL AND catsToInsert IS NOT NULL THEN
            FOREACH dltid IN ARRAY catsToDelete  LOOP
                DELETE FROM category_products WHERE product_id = prodid AND category_id = dltid;
              END LOOP;
            FOREACH insrtid IN ARRAY catsToInsert LOOP
                INSERT INTO category_products (product_id, category_id) VALUES(prodid, insrtid);
              END LOOP;
        END IF;
    END;
    $$ LANGUAGE plpgsql VOLATILE;
    */
    
    @Override
    public void updateProductCategories(ProductCategoriesUpdate pcu) {
        if (pcu == null || pcu.productId == null || pcu.oldCategoriesId == null || pcu.newCategoriesId == null 
                || (pcu.oldCategoriesId.isEmpty() && pcu.newCategoriesId.isEmpty())) {
            
            throw new AdminException().addMessage("Некорректное значение аргумента(ов). Ошибка валидации на контроллере.")
                    .addExceptionName("IllegalArgumentException");
        }
        
        try {            
            StringBuilder query = new StringBuilder();
            
            query.append("SELECT UPDATE_PRODUCT_CATEGORIES(").append(pcu.productId).append(",")
                        .append(PGUtil.getBigintArray(pcu.oldCategoriesId)).append(",")
                        .append(PGUtil.getBigintArray(pcu.newCategoriesId))
                        .append(")");
            
            jdbcTemplate.execute(query.toString());
            
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    /*
    CREATE OR REPLACE FUNCTION INSERT_CATEGORY(cname VARCHAR, cdesc VARCHAR)
    RETURNS VOID AS $$
    BEGIN
        PERFORM id FROM category  WHERE name = cname;
        IF NOT FOUND THEN 
            INSERT INTO category(name, description) VALUES (cname,cdesc);
            RETURN;
        END IF;
    END;
    $$ LANGUAGE plpgsql;
    */
    
    @Override 
    public boolean insertCategory(@Nullable String name, @Nullable String description) {
        if (name != null && description != null) {
            try {
                final String sql = "SELECT INSERT_CATEGORY(?,?);".intern();
                return jdbcTemplate.query(sql, new Object[] {name, description}, (ResultSet rs, int i) -> rs.getInt(1)).get(1) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean deleteCategory(@Nullable Long id) {
        if (id != null) {
            try {
                final String sql = "DELETE FROM category WHERE id = ?".intern() + id; 
                return jdbcTemplate.update(sql) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        } 
        return false;
    }
    
    /*
    CREATE OR REPLACE FUNCTION UPDATE_CATEGORY_NAME(cid BIGINT, cname VARCHAR)
    RETURNS VOID AS $$
    BEGIN
        PERFORM id FROM category  WHERE name = cname;
        IF NOT FOUND THEN 
            UPDATE category SET name = cname WHERE id = cid;
            RETURN;
        END IF;
    END;
    $$ LANGUAGE plpgsql;
    */
    
    @Override
    public boolean upadateCategoryName(@Nullable Long id, @Nullable String name) {
        if (id != null && name != null) {
            try {
                final String sql = "SELECT UPDATE_CATEGORY_NAME(?,?);".intern();
                return jdbcTemplate.update(sql, name) != 0;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean updateCategoryDesc(@Nullable Long id, @Nullable String desc) {
        if (id != null && desc != null) {
                try {
                    final String sql = "UPDATE category SET description = ? WHERE id =".intern() + id; 
                    return jdbcTemplate.update(sql, desc) == 1;
                } catch (DataAccessException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
            return false;
    }

    @Override
    public Category selectCategory(@Nullable Long id) {
        if (id != null) {
            try {
                final String sql = "SELECT * FROM category WHERE id = ".intern() + id; 
                final Category category = jdbcTemplate.query(sql, (ResultSet rs) 
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
    public List<Category> selectAllCategories() {
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
