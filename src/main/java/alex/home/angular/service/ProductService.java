package alex.home.angular.service;

import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Category;
import alex.home.angular.domain.Comment;
import alex.home.angular.domain.Img;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.utils.ConnectionUtil;
import alex.home.angular.utils.DateUtil;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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
                String sql = "SELECT p.*, i.url  FROM product AS p INNER JOIN img AS i ON p.img_id = i.id  WHERE p.id = ".intern() + id;
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

    @Override @Nullable @Transactional
    public Product selectProductCategoriesComments(@Nullable Long id) {
        if (id != null) {
            Product product = selectProductCategories(id);
            if (product == null) {
                return null;
            }
            try {
                String sql = "SELECT * FROM comment WHERE product_id = ".intern() + id;
                List<Comment> cmnts = jdbcTemplate.query(sql, (ResultSet rs, int i) -> 
                        new Comment(rs.getLong("id"), rs.getString("header") ,rs.getString("body"), rs.getString("nick"), rs.getDate("date")));
                product.comments = cmnts;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
            return product;
        }
        return null;
    }

    @Override @Nullable
    public List<Product> selectProductsWhereCtegoryId(@Nullable Long categoryId, @Nullable Integer limit, @Nullable Integer offset) {
        if (categoryId != null && limit != null && offset != null) {
            try {
                String sql = "SELECT p.*, i.url FROM product p INNER JOIN category_products cp ON "
                        + "cp.product_id = p.id AND cp.category_id = " + categoryId + " INNER JOIN img i ON p.img_id = i.id " + " LIMIT " + limit + " OFFSET " + offset * limit;
                List<Product> prdcts = jdbcTemplate.query(sql, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"),
                                rs.getInt("mark"), rs.getDouble("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"), rs.getDate("start"), 
                        rs.getDate("last"), rs.getString("url")));
                return prdcts;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    @Override @Nullable
    public List<Product> selectProductsWhereCategoryNotExist(@Nullable Integer limit, @Nullable Integer offset) {
        if (limit != null && offset != null) {
            try {
                String sql = String.format("SELECT * FROM product WHERE id NOT IN (SELECT id FROM category_products) LIMIT $n OFFSET $n;".intern(), limit, limit * offset);
                List<Product> prdcts = jdbcTemplate.query(sql, (ResultSet rs, int i) -> new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"),
                                    rs.getInt("mark"), rs.getDouble("price"), rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"),rs.getDate("start"), rs.getDate("last")));
                return prdcts;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    @Override @Nullable
    public List<Product> selectProductsInSomeTimePeriod(@Nullable Long mils, @Nullable Integer limit, @Nullable Integer offset) {
        if (mils != null && limit != null && offset != null) {
            try {
                String sql = "SELLECT * FROM product WHERE start > ? LIMIT ? OFFSET ?;".intern();
                List<Product> prdcts = jdbcTemplate.query(sql, new Object[] {DateUtil.getTimeStamp(mils), limit , limit * offset}, (ResultSet rs, int i) -> 
                        new Product(rs.getLong("id"), rs.getInt("buyStat"), rs.getInt("quant"), rs.getInt("mark"), rs.getDouble("price"), 
                                rs.getString("name"), rs.getString("description"), rs.getBoolean("exist"),rs.getDate("start"), rs.getDate("last")));
                return prdcts;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    /*
        SELECT DISTINCT table_name, column_name
        FROM information_schema.columns
        WHERE table_schema='public'
        AND table_name = 'PRODUCT';
    */
    
    /*
    {
    'where' : [
            'name' : null,                   LIKE %real symbols%,  IF NULL LIKE '%%'
            'description' : null,       LIKE %real symbols%,  IF NULL LIKE '%%'
            'price' : null,                    = n, > n, < n, > n0 and <n1  IF NULL > 0
            'mark' : null,                    = n, > n, < n, > n0 and <n1  IF NULL > 0
            'buystat ' : null,              = n, > n, < n, > n0 and <n1  IF NULL > 0
            'start' : null,                     > timestamp,  < timestamp, BEETWEN timestamp0 and timestamp1   IF NULL < now()
            'last' : null,                       > timestamp,  < timestamp, BEETWEN timestamp0 and timestamp1   IF NULL < now()
            'quant' : null,                   = n, > n, < n, > n0 and <n1  IF NULL > 0
            'exist' : null                      = 'true', <>  'true'     IF NULL  = 'true' OR <> 'true'
        ],
    'innerJoin' : {
        'categories' : [],                  IF NULL SELECT id FROM category 
        'operator' :                          IN, NOT IN
    }
    */
    
    /*
    SELECT attrelid ,attname ,atttypid ,attstattarget ,attlen ,attnum 
    ,attndims , attcacheoff , atttypmod ,attbyval , attstorage , attalign , attnotnull , atthasdef , attidentity FROM pg_attribute WHERE attrelid = 'PRODUCT'::regclass;
    */
    
    /*
    SELECT attisdropped ,attislocal ,attinhcount ,attcollation ,attacl ,attoptions, attfdwoptions FROM pg_attribute WHERE attrelid = 'PRODUCT'::regclass;
    */
    
    //atttypid - ТИП ДАННЫХ СТОЛБЦА
        //1043 - VARCHAR,  700 - REAL
    @Override
    public List<Product> productComplexSelection() {
        String[] categories = null;
        String operator = null;
        String[] where = null;
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT")
                .append((categories != null) ? " DISTINCT * FROM product p INNER JOIN category_products cp  ON p.id = cp.product_id AND  cp.product_id "
                        + operator + " " + categories : " * FROM product p");
        if (where != null) {
            sql.append(" WHERE ").append(where[0]);
            for (int i = 1; i < where.length; i++) {
                sql.append(" AND ").append(where[i]);
            }
        }
        sql.append(";");
                
        
                
      
        //SELECT DISTINCT p.* FROM product p INNER JOIN category_products cp  ON p.id = cp.product_id AND  cp.product_id IN (1,2,3,4,5,6,7);

        
        //AND name LIKE (%real symbol%  or  '%%')
        //AND description LIKE (%real symbol%  or  '%%')
        //AND price (> 5  OR price < 5 OR price  > 5 AND price  < 5)
        //
        return null;
    }
    
    @Override
    public boolean doSoldActions(@Nullable Long id, @Nullable Integer quant, @Nullable Integer buyStat, @Nullable Date lastBuy) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateProductName(@Nullable Long id, @Nullable String name) {
        if (id != null && name != null) {
            try {
                String sql = "UPDATE product SET name = ? WHERE id =".intern() + id;
                return jdbcTemplate.update(sql, name) == 1;
            } catch (DataAccessException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;    
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
    
    //OLD FUNCTION
    /*
    CREATE OR REPLACE FUNCTION INSERT_PRODUCT(prname VARCHAR, prdesc VARCHAR, prprice REAL, prquant INT, catid BIGINT) RETURNS INT AS $res$ DECLARE tid BIGINT; BEGIN PERFORM id FROM product WHERE name = prname; IF NOT FOUND THEN INSERT INTO product(name, description, price, quant, start) VALUES (prname, prdesc, prprice, prquant, NOW()); ELSE  RETURN 0; END IF; IF catid IS NULL THEN RETURN 1; ELSE PERFORM id FROM category WHERE id = catid;  IF FOUND THEN SELECT INTO tid id FROM product WHERE name = prname; INSERT INTO category_products (category_id, product_id) VALUES (catid, tid); RETURN 2; ELSE RETURN 1; END IF; END IF; END; $res$ LANGUAGE plpgsql;
    */
    
    @Override
    public boolean insertProduct(@Nullable InsertProdDto dto) {
        if (dto != null && dto.name != null && dto.url != null) {
            Connection con = null;
            try {
                DataSource ds = jdbcTemplate.getDataSource();
                if (ds == null) {
                    return false;
                }
                con = ds.getConnection();
                Array catIds = (dto.categoryIds != null) ? con.createArrayOf("BIGINT", dto.categoryIds) : null;
                String sql = "SELECT INSERT_PRODUCT(?, ?, ?, ?, ?, ?);".intern();
                jdbcTemplate.query(sql, new Object[] {dto.name, dto.description, dto.url, catIds , dto.price, dto.quantity}, (rs) -> {
                    return null;
                });
                return true;
            } catch (DataAccessException | SQLException  ex) {
                ex.printStackTrace();
            } finally {
                ConnectionUtil.closeConnection(con);
            }
        }
        return false;
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


}
