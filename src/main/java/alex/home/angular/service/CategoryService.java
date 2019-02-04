package alex.home.angular.service;

import alex.home.angular.annotation.NotNullArgs;
import alex.home.angular.dao.CategoryDao;
import alex.home.angular.domain.Category;
import alex.home.angular.dto.ProductCategoriesUpdate;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import alex.home.angular.utils.PGUtil;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
    public void updateProductCategories(ProductCategoriesUpdate pcu) {
        if (pcu == null || pcu.productId == null || pcu.oldCategoriesId == null || pcu.newCategoriesId == null 
                || (pcu.oldCategoriesId.isEmpty() && pcu.newCategoriesId.isEmpty())) {
            throw new AdminException().addMessage("Некорректное значение аргумента(ов).").addExceptionName("IllegalArgumentException");
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
    
    @Override @NotNullArgs
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
    public void deleteCategory(Long id) {
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
    BEGIN
        PERFORM id FROM category  WHERE name = cname;
        IF NOT FOUND THEN 
            UPDATE category SET name = cname WHERE id = cid;
            RETURN;
        END IF;
    END;
    $$ LANGUAGE plpgsql;
    */
    
    @Override @NotNullArgs
    public void upadateCategoryName(Long id, String name) {
        if (id == null || name == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage(name == null ? "String name  == null" : "" + id == null ? "Long id == null" : "");
        }

        try {
            jdbcTemplate.update("SELECT UPDATE_CATEGORY_NAME(?,?);", id, name);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @NotNullArgs
    public void updateCategoryDesc(Long id, String description) {
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
    public void updateCategory(Category category) {
        if (category == null || category.id == null || category.name == null || category.description == null) {
                throw new AdminException().addMessage("@NotNull rguments: " + category == null ? "category == null  " : "" + category != null 
                        ? category.id == null ? "category.id == null  " : "" + category.name == null ? "category.name == null  " : "" + category.description == null
                                ? "category.description == null" : "" : "").addExceptionName("IllegalAtributeException");
        }
        System.out.println(category.description);
        try {
            jdbcTemplate.update("UPDATE " + PGMeta.CATEGORY_TABLE + " SET name = ?, description = ? WHERE id = " + category.id, category.name, category.description);
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }
    
    @Override
    public Category selectCategory(Long id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Long id == null");
        }
        
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM category WHERE id = " + id, (ResultSet rs, int i)
                    -> { return new Category(rs.getLong("id"), rs.getString("name"), rs.getString("description")); });
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }
    

    @Override
    public List<Category> selectAllCategories() {
        try {
          return jdbcTemplate.query("SELECT * FROM category;", (ResultSet rs, int i) -> new Category(rs.getLong("id"), rs.getString("name"), rs.getString("description")));
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
