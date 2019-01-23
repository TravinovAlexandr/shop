package alex.home.angular.service;

import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Category;
import alex.home.angular.domain.Comment;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductRow;
import alex.home.angular.exception.AdminException;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService implements ProductDao {
   
    private JdbcTemplate jdbcTemplate;
    
    @Override @Nullable
    public Product selectProduct(@Nullable Long id) {
        if (id != null) {
            try {
                String sql = "SELECT * FROM product WHERE id = ".intern() + id;
                Product product = jdbcTemplate.queryForObject(sql, (ResultSet rs, int i)
                        -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"),
                                rs.getInt("mark"), rs.getDouble("price"), rs.getString("name"), rs.getString("description"), 
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
    public Product selectProductWithImage(@Nullable Long id) {
        if (id != null) {
            try {
                String sql = "SELECT p.*, i.url  FROM product AS p JOIN img AS i ON p.img_id = i.id  WHERE p.id = "+ id;
                Product product = jdbcTemplate.queryForObject(sql, (ResultSet rs, int i)
                        -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"),
                                rs.getInt("mark"), rs.getDouble("price"), rs.getString("name"), rs.getString("description"), 
                                rs.getBoolean("exist"), rs.getDate("start"), rs.getDate("last"), rs.getString("url")));
                return product;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override @Nullable @Transactional
    public Product selectProductCategories(@Nullable Long id) {
        if (id != null) {
            try {
                String sql = "SELECT c.* FROM category c INNER JOIN category_products cp ON cp.category_id = c.id AND cp.product_id =" + id;
                Product product = selectProductWithImage(id);
                List<Category> cats = jdbcTemplate.query(sql, (ResultSet rs, int i) -> new Category(rs.getLong(1), rs.getString(2), rs.getString(3)));
                if (product == null || cats == null) {
                    return null;
                }
                product.category = cats;
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
        AdminException exception = new AdminException();
        
        if (id == null) {
            throw exception.addExceptionName("IllegalArgumentException")
                    .addMessage("Переданный id = NULL. Запрос: /admin/product/{id}. Ошибка валидации на уровне контроллера.");
        }
        
        try {
            List<Category> categories = new ArrayList<>();
            List<Comment> comments = new ArrayList<>();
            Product product = new Product();
            EntityCheck check = new EntityCheck(2);
            String query = "SELECT p.*, i.url, c.id AS cat_id, c.name AS cat_name, "
                    + "cm.id  AS cm_id, cm.nick AS cm_nick, cm.date AS cm_date, cm.header AS cm_header, cm.body AS cm_body  FROM product p "
                    + "LEFT JOIN img i ON  p.img_id = i.id "
                    + "LEFT JOIN category_products cp ON p.id = cp.product_id "
                    + "LEFT JOIN category c ON c.id = cp.category_id "
                    + "LEFT JOIN coments cm ON cm.product_id = p.id WHERE p.id = " + id;
            
            jdbcTemplate.query(query, (ResultSet rs, int i) -> {
                if (check.isInit == false) {
                    product.id = rs.getLong("id"); 
                    product.buyStat = rs.getInt("buyStat");
                    product.quantity = rs.getInt("quant");
                    product.mark = rs.getInt("mark");
                    product.price = rs.getDouble("price"); 
                    product.name = rs.getString("name"); 
                    product.description = rs.getString("description");
                    product.isExist = rs.getBoolean("exist"); 
                    product.startDate = rs.getDate("start"); 
                    product.lastBuyDate = rs.getDate("last");
                    product.imgUrl =  rs.getString("url");
                    product.imgId = rs.getLong("img_id");
                    check.init();
                }
                
                Category category =  new Category(rs.getLong("cat_id"), rs.getString("cat_name"));
                
                if (!check.isLastEntity(0, category.id)) {
                    categories.add(category);
                }
                
                Comment comment = new Comment(rs.getLong("cm_id"), rs.getString("cm_header"), rs.getString("cm_body"), rs.getString("cm_nick"), rs.getDate("cm_date"));
                
                if (!check.isLastEntity(1, comment.id)) {
                    comments.add(comment);
                }
                
                return product;
            });
            
            product.category = categories;
            product.comments = comments;
            return product;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw exception.addExceptionName(ex.getClass().getSimpleName()).addSTrace(ex);
        }
    }

    @Override @Nullable
    public List<Product> selectProductsWhereCtegoryId(@Nullable Long categoryId, @Nullable Integer limit, @Nullable Integer offset) {
        if (categoryId != null && limit != null && offset != null) {
            try {
                String sql = "SELECT p.*, i.url FROM product p INNER JOIN category_products cp ON cp.product_id = p.id AND cp.category_id = " + categoryId 
                        + " INNER JOIN img i ON p.img_id = i.id " + " LIMIT " + limit + " OFFSET " + offset * limit;
                
                return jdbcTemplate.query(sql, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"),
                                rs.getInt("mark"), rs.getDouble("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getDate("start"), 
                        rs.getDate("last"), rs.getString("url")));
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
        return null;
    }
    
    @Override @NotNull
    public List<Product> selectProductsWhereCategoryNotExist(@Nullable Integer limit, @Nullable Integer offset) {
        if (limit == null && offset == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException.")
                    .addMessage("Переданные аргументы == NULL. Ошибка валидации на уровне контроллера.");
        }
        
        try {
            String sql = String.format("SELECT * FROM product WHERE id NOT IN (SELECT id FROM category_products) LIMIT $n OFFSET $n;", limit, limit * offset);

            return jdbcTemplate.query(sql, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"), rs.getDouble("price"), 
                    rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getDate("start"), rs.getDate("last")));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @Nullable
    public List<Product> selectProductsInSomeTimePeriod(@NotNull Long mils, @NotNull Integer limit, @NotNull Integer offset) {
            try {
                String sql = "SELLECT * FROM product WHERE start > ? LIMIT ? OFFSET ?;".intern();
                List<Product> prdcts = jdbcTemplate.query(sql, new Object[] {DateUtil.getTimeStamp(mils), limit , limit * offset}, (ResultSet rs, int i) -> 
                        new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"), rs.getDouble("price"), 
                                rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"),rs.getDate("start"), rs.getDate("last")));
                return prdcts;
            } catch (NullPointerException | DataAccessException ex) {
                ex.printStackTrace();
            }
        return null;
    }
    
    @Override @NotNull
    public List<ProductRow> searchTableSelection(@NotNull String query) {
        if (query == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException")
                    .addMessage("NULL значение строки запроса не предусмотренно. Ошибка валидации на уровне контроллера.");
        }
        
        try {
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> new ProductRow(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"), 
                    rs.getDouble("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), 
                    DateUtil.getDate(rs.getDate("start")), DateUtil.getDate(rs.getDate("last"))));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException().addExceptionName(ex.getClass().getSimpleName()).addSTrace(ex);
        }
    }
    
    @Override
    public boolean doSoldActions(@Nullable Long id, @Nullable Integer quant, @Nullable Integer buyStat, @Nullable Date lastBuy) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override @NotNull
    public boolean updateProductName(@NotNull Long id, @NotNull String name) {
        try {
            String sql = "UPDATE product SET name = ? WHERE id =".intern() + id;
            return jdbcTemplate.update(sql, name) == 1;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException().addExceptionName(ex.getClass().getSimpleName()).addSTrace(ex);
        }
    }
    

    @Override
    public boolean updateProductDesc(@Nullable Long id, @Nullable String desc) {
        if (id != null && desc != null) {
            try {
                String sql = "UPDATE product SET description =? WHERE id =".intern() + id;
                return jdbcTemplate.update(sql, desc) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
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
    public boolean updateProductCategory(@Nullable Long id ,@Nullable Long oldCategoryId, @Nullable Long newCategoryId) {
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

    @Override
    public boolean updateProductPrice(@Nullable Long id, @Nullable Double price) {
        if (id != null && price != null) {
            try {
                String sql = "UPDATE product SET price =" + price + " WHERE id =" + id;
                return jdbcTemplate.update(sql) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;        
    }

    @Override
    public boolean updateProductQuant(@Nullable Long id, @Nullable Integer quant) {
        if (id != null && quant != null) {
            try {
                String sql = "UPDATE product SET quant =" + quant + " WHERE id =" + id;
                return jdbcTemplate.update(sql) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;        
    }

    @Override
    public boolean updateProductMark(@Nullable Long id, @Nullable Integer mark) {
        if (id != null && mark != null) {
            try {
                String sql = "UPDATE product SET mark =" + mark + " WHERE id =" + id;
                return jdbcTemplate.update(sql) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
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
                String sql = "SELECT INSERT_PRODUCT(?, ?, ?, ?, ?, ?);";
                
                jdbcTemplate.query(sql, new Object[]{dto.name, dto.description, dto.url, catIds, dto.price, dto.quantity}, (rs) -> {});
            }
            
        } catch (DataAccessException | SQLException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        } finally {
            ConnectionUtil.closeConnection(con);
        }
    }

    @Override
    public boolean deleteProduct(@Nullable Long id) {
        if (id != null) {
            try {
                String sql = "DELETE FROM product WHERE id = " + id;
                return jdbcTemplate.update(sql) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            } 
        } 
        return false;    
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
