package alex.home.angular.service;

import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Category;
import alex.home.angular.domain.Comment;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import alex.home.angular.annotation.*;
import alex.home.angular.utils.PGUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService implements ProductDao {
   
    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean incrementProductMark(@NotNull Long id) {
        if (id == null) {
            return false;
        }
        try {
            return jdbcTemplate.update("UPDATE "+ PGMeta.PRODUCT_TABLE +" SET mark = mark + 1 WHERE id = " + id) != 0; 
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override @NotNull
    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Product selectProduct(@NotNull Long id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }

        try {
            return jdbcTemplate.queryForObject("SELECT * FROM product WHERE id = " + id, (ResultSet rs, int i) -> new Product(rs.getLong("id"),rs.getInt("buyStat"), rs.getInt("quant"), 
                    rs.getInt("mark"), rs.getFloat("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getBoolean("recommend"), rs.getDate("start"), rs.getDate("last")));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @Nullable
    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public Product selectProductCategoriesComments(@Nullable Long id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }

        try {
            List<Category> categories = new ArrayList<>();
            List<Comment> comments = new ArrayList<>();
            Product product = new Product();
            String query = "SELECT p.*, i.url, c.id AS cat_id, c.name AS cat_name, "
                    + "cm.id  AS cm_id, cm.nick AS cm_nick, cm.date AS cm_date, cm.body AS cm_body  FROM product p "
                    + "LEFT JOIN img i ON  p.img_id = i.id "
                    + "LEFT JOIN category_products cp ON p.id = cp.product_id "
                    + "LEFT JOIN category c ON c.id = cp.category_id "
                    + "LEFT JOIN coments cm ON cm.product_id = p.id WHERE p.id = " + id;
            
            jdbcTemplate.query(query, (ResultSet rs, int i) -> {    
                if (i == 0) {
                    product.id = rs.getLong("id"); 
                    product.buyStat = rs.getInt("buyStat");
                    product.quantity = rs.getInt("quant");
                    product.mark = rs.getInt("mark");
                    product.price = rs.getFloat("price"); 
                    product.name = rs.getString("name"); 
                    product.description = rs.getString("description");
                    product.isExist = rs.getBoolean("exist"); 
                    product.isRecommend = rs.getBoolean("recommend");
                    product.startDate = rs.getDate("start"); 
                    product.lastBuyDate = rs.getDate("last");
                    product.imgUrl =  rs.getString("url");
                    product.imgId = rs.getLong("img_id");
                }
                
                Category category =  new Category(rs.getLong("cat_id"), rs.getString("cat_name"));
                if (!categories.contains(category)) {
                    categories.add(category);
                }
                
                Comment comment = new Comment(rs.getLong("cm_id"), rs.getString("cm_body"), rs.getString("cm_nick"), rs.getDate("cm_date"));
                if (!comments.contains(comment)) {
                    comments.add(comment);
                }
                
                return product;
            });
            
            product.category = categories;
            product.comments = comments;
            return product;
        } catch (DataAccessException  ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    @Override @Nullable
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Product> selectProductsWhereCtegoryId(String query) {
        if (query == null) {
            return null;
        }

        try {
        return jdbcTemplate.query(query, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"), 
                rs.getFloat("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getBoolean("recommend"), rs.getDate("start"),rs.getDate("last"), rs.getString("url")));
        } catch (DataAccessException ex) {
                ex.printStackTrace();
                throw ex;
            }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Long> selectIds(String query) {
        if (query == null) {
            return null;
        }

        try {
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> { return rs.getLong("id"); });
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Product> selectLastAdded(@NotNull String query) {
        if (query == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }

        try {
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"),
                    rs.getFloat("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getBoolean("recommend"), rs.getDate("start"), rs.getDate("last"), rs.getString("url")));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @NotNull
    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Product> selectRecommended(@NotNull Integer limit) {
        if (limit == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
            return jdbcTemplate.query("SELECT p.*, i.url FROM product WHERE recommend = 't' LIMIT " + limit, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), 
                    rs.getInt("quant"), rs.getInt("mark"),rs.getFloat("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getBoolean("recommend"), 
                    rs.getDate("start"), rs.getDate("last"), rs.getString("url")));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @NotNull
    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public List<Product> searchFormSelection(@NotNull String query) {
        if (query == null) {
            throw new AdminException().addMessage("Controller validation args error.").addExceptionName("IllegalArgumentException");
        }
        
        try {
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"),rs.getFloat("price"), 
                    rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getBoolean("recommend"),rs.getDate("start"), rs.getDate("last")));
        } catch (DataAccessException | IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @Nullable
    @Transactional(propagation = Propagation.MANDATORY)
    public Integer getProductCount(String query) {
        if (query == null) {
            return null;
        }
        try {
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> rs.getInt("count")).stream().findFirst().orElse(0);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }

    /* 
    CREATE OR REPLACE FUNCTION UPDATE_PRODUCT_CATEGORY(productid BIGINT, newcategoryId BIGINT, oldcategoryid BIGINT)
    RETURNS VOID AS $$
    DECLARE 
        catid BIGINT;
        prid BIGINT;
    BEGIN
        SELECT INTO catid id FROM category WHERE id = newcategoryId FOR UPDATE;
        SELECT INTO prid id FROM product WHERE id = product FOR UPDATE;
            IF catid IS NOT NULL AND prid IS NOT NULL THEN
                UPDATE category_products SET category_id = newcategoryid, product_id = productid WHERE category_id = oldcategoryid; 
            END IF;
    END;
    $$ LANGUAGE plpgsql VOLATILE;
    */
    
    @Override @NullableArgs
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean updateProductCategories(Long id, Long oldCategoryId, Long newCategoryId) {
        if (id == null || oldCategoryId == null || newCategoryId == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        try {
            return jdbcTemplate.update(String.format("UPDATE_PRODUCT_CATEGORY(%n, %n, %n)", id, newCategoryId, oldCategoryId)) == 1;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public <T, E> void updateSingleField(T cond, E val, String query, String exName, String exMessage) {
        if (cond == null || val == null || query == null) {
            throw new AdminException().addExceptionName(exName == null ? "" : exName).addMessage(exMessage == null ? "" : exMessage);
        }
        
        try {
            jdbcTemplate.update(query, val, cond);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    /*
    CREATE OR REPLACE FUNCTION INSERT_PRODUCT(prname VARCHAR, prdesc VARCHAR, imgurl  VARCHAR, categids BIGINT[], prprice REAL, prquant INT ) 
    RETURNS VOID AS $$
    DECLARE
        imageid BIGINT;
        productid BIGINT;
        categoryid BIGINT;
    BEGIN
        INSERT INTO img (url) VALUES (imgurl);
        IF FOUND THEN 
            SELECT INTO imageid currval('"img_id_seq"');
            INSERT INTO product(name,description, price, quant, exist, start, img_id) VALUES (prname, prdesc, prprice, prquant, 'true', NOW(), imageid);
            IF FOUND THEN
                IF categids IS NOT NULL THEN
                    SELECT INTO productid last_value FROM product_id_seq;
                    FOREACH categoryid IN ARRAY categids
                    LOOP
                        INSERT INTO category_products (product_id, category_id) VALUES(productid, categoryid);
                    END LOOP;
                END IF;
            END IF;
        END IF;
    END;
    $$ LANGUAGE plpgsql VOLATILE;
    */
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void insertProduct(@NotNull InsertProdDto dto) {
        if (dto == null || dto.name == null || dto.url == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
            jdbcTemplate.query("SELECT INSERT_PRODUCT(?, ?, ?, " + PGUtil.getBigintArray(dto.categoryIds) +" , ?, ?);", 
                    new Object[]{dto.name, dto.description, dto.url, dto.price, dto.quantity}, (rs) -> {});
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        } 
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteProduct(@NotNull Long id) {
        if (id == null) {
             throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        try {
            jdbcTemplate.update("DELETE FROM product WHERE id = " + id);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateRecommend(String query) {
        if (query == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
            jdbcTemplate.update(query);
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
        }
    }

    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
