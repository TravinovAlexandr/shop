package alex.home.angular.service;

import alex.home.angular.annotation.NotNullArgs;
import alex.home.angular.dao.CategoryDao;
import alex.home.angular.domain.Category;
import alex.home.angular.dto.ProductCategoriesUpdate;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import alex.home.angular.utils.SqlUtil;
import java.sql.ResultSet;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService implements CategoryDao {
    
    private JdbcTemplate jdbcTemplate;
    
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
    
    @Override @NotNullArgs
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateProductCategories(ProductCategoriesUpdate pcu) {
        if (pcu == null || pcu.productId == null || pcu.oldCategoriesId == null || pcu.newCategoriesId == null || pcu.oldCategoriesId.isEmpty() && pcu.newCategoriesId.isEmpty()) {
            throw new AdminException().addMessage("Controller validation args error").addExceptionName("IllegalArgumentException");
        }
        
        try {            
            StringBuilder query = new StringBuilder();
            
            query.append("SELECT UPDATE_PRODUCT_CATEGORIES(").append(pcu.productId).append(",")
                        .append(SqlUtil.getIntArray(pcu.oldCategoriesId)).append(",")
                        .append(SqlUtil.getIntArray(pcu.newCategoriesId))
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
    $$ LANGUAGE plpgsql  VOLATILE;
    */
    
    @Override @NotNullArgs
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void insertCategory(String name, String description) {
        if (name == null || description == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage(name == null ? "String name == null" : ""
                    + description == null ? "String description == null" : "");
        }

        try {
            jdbcTemplate.queryForObject("SELECT INSERT_CATEGORY(?,?);", new Object[]{name, description}, (ResultSet rs, int i) -> {return null;});
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public void deleteCategory(@NotNull Integer id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Long id == null");
        }
            try {
                jdbcTemplate.update("DELETE FROM " + PGMeta.CATEGORY_TABLE + " WHERE id = " + id);
            } catch (DataAccessException ex) {
                throw new AdminException(ex);
            }
        }
    
    /*
    CREATE OR REPLACE FUNCTION UPDATE_CATEGORY_NAME(cid BIGINT, cname VARCHAR)
    RETURNS VOID AS $$
    DECLARE
        isExist VARCHAR;
    BEGIN
        PERFORM id FROM category  WHERE id = cid;
        IF FOUND THEN
            SELECT INTO isExist name FROM category WHERE name = cname FOR UPDATE;
            IF  isExist IS NULL THEN
                UPDATE category SET name = cname WHERE id = cid;
            END IF;
        END IF;
    END;
    $$ LANGUAGE plpgsql VOLATILE;
    */
    
    @Override @NotNullArgs
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void upadateCategoryName(Integer id, String name) {
        if (id == null || name == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage(name == null ? "String name  == null" : "" + id == null ? "Long id == null" : "");
        }

        try {
            jdbcTemplate.update("SELECT SUPDATE_CATEGORY_NAME(?,?);", id, name);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @NotNullArgs
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public void updateCategoryDesc(Integer id, String description) {
        if (id == null || description == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("@NotNullArgs: " +description == null ? "String description == null  " : ""
                    + id == null ? "Long id == null" : "");
        }

        try {
            jdbcTemplate.update("UPDATE " + PGMeta.CATEGORY_TABLE + " SET description = ? WHERE id = " +  id, description);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    
    @Override
    @Transactional( propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateCategory(Category category) {
        if (category == null || category.id == null || category.name == null || category.description == null) {
                throw new AdminException().addMessage("@NotNull rguments: " + category == null ? "category == null  " : "" + category != null 
                        ? category.id == null ? "category.id == null  " : "" + category.name == null ? "category.name == null  " : "" + category.description == null
                                ? "category.description == null" : "" : "").addExceptionName("IllegalAtributeException");
        }

        try {
            jdbcTemplate.update("UPDATE " + PGMeta.CATEGORY_TABLE + " SET name = ?, description = ? WHERE id = " + category.id, category.name, category.description);
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Category selectCategory(Integer id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Long id == null");
        }
        
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM category WHERE id = " + id, (ResultSet rs, int i) 
                    -> { return new Category(rs.getInt("id"), rs.getInt("pid"), rs.getString("name"), rs.getString("description")); });
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }
    

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Category> selectAllCategories() {
        try {
          return jdbcTemplate.query("SELECT * FROM category;", (ResultSet rs, int i) -> new Category(rs.getInt("id"), rs.getInt("pid"), rs.getString("name"), rs.getString("description")));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
