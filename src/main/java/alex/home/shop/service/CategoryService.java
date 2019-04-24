package alex.home.shop.service;

import alex.home.shop.annotation.NotNullArgs;
import alex.home.shop.dao.CategoryDao;
import alex.home.shop.domain.Category;
import alex.home.shop.dto.ProductCategoriesUpdate;
import alex.home.shop.exception.AdminException;
import alex.home.shop.sql.PGMeta;
import alex.home.shop.utils.SqlUtil;
import alex.home.shop.utils.ValidationUtil;
import java.sql.ResultSet;
import java.util.List;
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
            query.append("SELECT UPDATE_PRODUCT_CATEGORIES(").append(pcu.productId).append(",").append(SqlUtil.getIntArray(pcu.oldCategoriesId)).append(",")
                        .append(SqlUtil.getIntArray(pcu.newCategoriesId)).append(")");
            
            jdbcTemplate.execute(query.toString());
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Override @NotNullArgs
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Integer insertCategory(Integer pid, String name, String description, Boolean isLeaf) {
        if (pid == null || name == null || description == null || isLeaf == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage(name == null ? "String name == null" : "" + description == null ? "String description == null" : "");
        }

        try {
            Integer oldId = jdbcTemplate.query("SELECT  id FROM  " + PGMeta.CATEGORY_TABLE + "  WHERE name = ?", new Object[] { name }, (ResultSet rs, int i) -> { return rs.getInt("id"); }).stream().findAny().orElse(null);
            if (oldId != null) {
                throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Not unique category name.");
            } 
            
            return jdbcTemplate.query("INSERT INTO  " + PGMeta.CATEGORY_TABLE + "  (pid,name,description,leaf)VALUES("+ pid + ",?,??) RETURNING id", new Object [] { name, description, isLeaf }, (ResultSet rs, int i) 
                    -> rs.getInt("id")).stream().findFirst().orElse(null);
            } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public boolean deleteCategory(Integer id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Long id == null");
        }

        try {
            return jdbcTemplate.update("DELETE FROM " + PGMeta.CATEGORY_TABLE + " WHERE id = " + id + "  AND NOT EXISTS (SELECT id FROM  " 
                    + PGMeta.CATEGORY_TABLE + " WHERE pid = " + id + ")") != 0;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Override @NotNullArgs
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void upadateCategoryName(Integer id, String name) {
        if (id == null || name == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage(name == null ? "String name  == null" : "" + id == null ? "Long id == null" : "");
        }

        try {
            jdbcTemplate.update("UPDATE " + PGMeta.CATEGORY_TABLE + " SET name = ? WHERE id = " + id + " AND NOT EXISTS (SELECT FROM  " 
                    + PGMeta.CATEGORY_TABLE + " WHERE name = ?)", name, name);
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public boolean updateCategory(Category category) {
        if (!ValidationUtil.validateNull(category, category.id, category.pid) || !ValidationUtil.validateEmptyString(category.name, category.description)) {
                throw new AdminException().addExceptionName("IllegalAtributeException").addMessage("Controller validation ex");
        }
        
        try {
            List<Integer> pids = jdbcTemplate.query("SELECT pid FROM "  + PGMeta.CATEGORY_TABLE +  " WHERE id <> " + category.id, (ResultSet rs, int i ) -> rs.getInt("pid"));
            boolean isLeaf = pids.contains(category.id);
            
            return jdbcTemplate.update("UPDATE " + PGMeta.CATEGORY_TABLE + " SET name = ?, pid = " + category.pid + ", description = ?, leaf = ? WHERE id = " + category.id 
                    + " AND ? <> any (SELECT name FROM category WHERE id <> " + category.id + ")", category.name, category.description, isLeaf, category.name) > 0;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public boolean isCategoryNameExist(Integer catId, String catName) {
        if (catId == null || catName == null) {
            throw new AdminException().addExceptionName("IllegalAtributeException").addMessage("Controller validation ex");
        }
        
        try {
            return jdbcTemplate.query("SELECT id FROM category WHERE name = ? AND id <>" + catId, new Object [] { catName }, (ResultSet rs, int i) -> 
                    rs.getInt("id")).stream().findAny().orElse(null) != null;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public boolean isCategoryPidExist(String catPid) {
        if (catPid == null) {
            throw new AdminException().addExceptionName("IllegalAtributeException").addMessage("Controller validation ex");
        }
        
        try {
            return jdbcTemplate.query("SELECT id FROM category WHERE id = " + catPid, (ResultSet rs, int i) -> rs.getInt("id")).stream().findFirst().orElse(null) == null;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
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
                    -> { return new Category(rs.getInt("id"), rs.getInt("pid"), rs.getString("name"), rs.getString("description"), rs.getBoolean("leaf")); });
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }
    

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Category> selectAllCategories() {
        try {
          return jdbcTemplate.query("SELECT * FROM category;", (ResultSet rs, int i) -> new Category(rs.getInt("id"), rs.getInt("pid"), rs.getString("name"), rs.getString("description"), rs.getBoolean("leaf")));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Category> selectLeafCategories() {
        try {
            return jdbcTemplate.query("SELECT * FROM category WHERE leaf = 'T';", (ResultSet rs, int i) -> new Category(rs.getInt("id"), rs.getInt("pid"), rs.getString("name"), rs.getString("description"), rs.getBoolean("leaf")));
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
