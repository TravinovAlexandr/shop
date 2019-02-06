package alex.home.angular.service;

import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Category;
import alex.home.angular.domain.Comment;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductRow;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import alex.home.angular.utils.ConnectionUtil;
import alex.home.angular.utils.DateUtil;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import alex.home.angular.annotation.*;

@Service
public class ProductService implements ProductDao {
   
    private JdbcTemplate jdbcTemplate;
    
    @Override
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
    
    @Override @Nullable
    public Product selectProduct(@Nullable Long id) {
        if (id != null) {
            try {
                String sql = "SELECT * FROM product WHERE id = ".intern() + id;
                Product product = jdbcTemplate.queryForObject(sql, (ResultSet rs, int i)
                        -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"),
                                rs.getInt("mark"), rs.getFloat("price"), rs.getString("name"), rs.getString("description"), 
                                rs.getBoolean("exist"), rs.getDate("start"), rs.getDate("last")));
                return product;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    @Override @Nullable
    public Product selectProductCategoriesComments(@Nullable Long id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException")
                    .addMessage("Переданный id = NULL. Запрос: /admin/product/{id}. Ошибка валидации на уровне контроллера.");
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
            throw new AdminException().addExceptionName(ex.getClass().getSimpleName()).addSTrace(ex);
        }
    }

    @Override @Nullable @NotNullArgs
    public List<Product> selectProductsWhereCtegoryId(Long categoryId, Integer limit, Integer offset) {
        if (categoryId == null || limit == null || offset == null) {
            return null;
        }

        try {
        String query = "SELECT p.*, i.url FROM  product p JOIN img i ON p.img_id =  i.id JOIN category_products cp ON cp.product_id = p.id WHERE cp.category_id = " 
                + categoryId +" LIMIT " + limit+" OFFSET " + offset;
        
        return jdbcTemplate.query(query, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"), 
                rs.getFloat("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getDate("start"), 
                rs.getDate("last"), rs.getString("url")));
        } catch (DataAccessException ex) {
                ex.printStackTrace();
                throw ex;
            }
    }
    
    @Override @NotNull
    public List<ProductRow> searchFormsSelection(@NotNull String query) {
        if (query == null) {
            throw new AdminException().addMessage("@NotNull String query == null.")
            .addExceptionName("IllegalArgumentException");
        }
        
        try {
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> new ProductRow(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"), 
                    rs.getDouble("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"),DateUtil.getDate(rs.getDate("start")), 
                    DateUtil.getDate(rs.getDate("last")), rs.getInt("count")));
        } catch (DataAccessException | IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @Nullable
    public Integer getProductCount(String query) {
        if (query == null) {
            return null;
        }
//        "SELECT COUNT(price) FROM " + PGMeta.PRODUCT_TABLE, (ResultSet rs, int i) -> rs.getInt("count")
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
    BEGIN
        PERFORM id FROM category WHERE id = newcategoryId;
            IF FOUND THEN
                UPDATE category_products SET category_id = newcategoryid, product_id = productid WHERE category_id = oldcategoryid; 
            END IF;
    END;
    $$ LANGUAGE plpgsql;
    */
    
//    CREATE OR REPLACE FUNCTION UPDATE_PRODUCT_CATEGORY(productid BIGINT, newcategoryId BIGINT, oldcategoryid BIGINT) RETURNS VOID AS $$ BEGIN SELECT id FROM category WHERE id = newcategoryId; IF FOUND THEN UPDATE category_products SET category_id = newcategoryid, product_id = productid WHERE category_id = oldcategoryid; END IF; END; $$ LANGUAGE plpgsql;
    
    @Override
    public boolean updateProductCategories(@Nullable Long id ,@Nullable Long oldCategoryId, @Nullable Long newCategoryId) {
        if (id != null && oldCategoryId != null && newCategoryId != null) {
           try {
               String sql = String.format("UPDATE_PRODUCT_CATEGORY(%n, %n, %n)", id, newCategoryId, oldCategoryId);
               return jdbcTemplate.update(sql) == 1;
           } catch(DataAccessException ex) {
               ex.printStackTrace();
               return false;
           }
        }
        return false;
    }
    
    private <T, E> void updateSingleField(T cond, E val, String query, String exName, String exMessage) {
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
    
    @Override
    public void updateProductName(Long id, String name) {
        updateSingleField(id, name, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET name = ? WHERE id = ?", 
                "IllegalArgumentException", "@NotNull Long id, @NotNull String name");
    }
    
    @Override
    public void updateProductDesc(Long id, String desc) {
        updateSingleField(id, desc, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET description = ? WHERE id = ?", 
                "IllegalArgumentException", "@NotNull Long id, @NotNull String desc");
    }
    
    @Override
    public void updateProductPrice(Long id, Float price) {
        updateSingleField(id, price, "UPDATE "  + PGMeta.PRODUCT_TABLE +  " SET price = ? WHERE id = ?" , 
                "IllegalArgumentException", "@NotNull Long id, @NotNull Float price");
    }
    
    @Override
    public void updateProductQuant(@NotNull Long id, @NotNull Integer quant) {
        updateSingleField(id, quant, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET quant = ? WHERE id = ?" , 
                "IllegalArgumentException", "@NotNull Long id, @NotNull Integer quant");
    }
    
    @Override
    public void updateProductMark(@NotNull Long id, @NotNull Integer mark) {
        updateSingleField(id, mark, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET mark = ? WHERE id = ?"  , 
                "IllegalArgumentException", "@NotNull Long id, @NotNull Integer mark");
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
    $$ LANGUAGE plpgsql;
    */
    
    @Override
    public void insertProduct(@NotNull InsertProdDto dto) {
        if (dto == null || dto.name == null || dto.url == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException")
                    .addMessage("@Nullable InsertProdDto dto == null || dto.name == null || dto.url == null. Ошибка валидации на уровне контроллера.");
        }
        
        Connection con = null;
        
        try {
            DataSource ds = jdbcTemplate.getDataSource();
            
            if (ds != null) {
                con = ds.getConnection();
                Array catIds = (dto.categoryIds != null) ? con.createArrayOf("BIGINT", dto.categoryIds) : null;
                
                jdbcTemplate.query("SELECT INSERT_PRODUCT(?, ?, ?, ?, ?, ?);", new Object[]{dto.name, dto.description, dto.url, catIds, dto.price, dto.quantity}, (rs) -> {});
            }
            
        } catch (DataAccessException | SQLException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        } finally {
            ConnectionUtil.closeConnection(con);
        }
    }

    @Override
    public void deleteProduct(@NotNull Long id) {
        if (id == null) {
             throw new AdminException().addExceptionName("IllegalArgumentException")
                    .addMessage("@NotNull Long id == null. Ошибка валидации на уровне контроллера.");
        }
        
        try {
            jdbcTemplate.update("DELETE FROM product WHERE id = " + id);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    } 


    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private static class EntityCheck {
        
        private boolean isInit = false;
        private final List<Number> ids = new ArrayList<>();
        
        public EntityCheck() {}
        
        public EntityCheck(Integer elemQuant) {
            for (int i = 0; i < elemQuant; i++) {
                ids.add(-1);
            }
        }
        
        public  void init() {
            isInit = true;
        }
        
        public boolean isInit() {
            return isInit;
        }
        
        public boolean isConteins(List<Number> l, Number t) {
            return t == null || l.contains(t);
        }  
        
        public boolean isLastEntity(int entityIndex, Long value) {
            if (entityIndex > ids.size()) {
                throw new ArrayIndexOutOfBoundsException();
            } else if (value == null || value.equals(0L)) {
                return true;
            } else if (ids.get(entityIndex).equals(value)) {
                return true;
            } else {
                ids.set(entityIndex, value);
                return false;
            }
        }
    }

}
