package alex.home.shop.service;

import alex.home.shop.dao.ProductDao;
import alex.home.shop.domain.Category;
import alex.home.shop.domain.Comment;
import alex.home.shop.domain.Img;
import alex.home.shop.domain.Product;
import alex.home.shop.domain.Tag;
import alex.home.shop.dto.InsertProdDto;
import alex.home.shop.exception.AdminException;
import alex.home.shop.sql.PGMeta;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import alex.home.shop.utils.SqlUtil;
import alex.home.shop.utils.ValidationUtil;
import java.sql.SQLException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService implements ProductDao {

    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Product> selectMinProducts(String query) {
        try {
            
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> getMinProduct(rs));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean incrementProductMark(Integer id) {
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
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Product selectProductImgsCommentsTags(Integer id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }

        try {
            Product product = new Product();
            List<Img> imgs = new ArrayList<>();
            List<Tag> tags = new ArrayList<>();
            List<Comment> comments = new ArrayList<>();
            String query = "SELECT DISTINCT p.id, p.mark, p.discount, p.price, p.prev_price, p.weight, p.length, p.width, p.height, p.volume, p.exist, p.name AS prname, "
                    + "p.description AS prdesc, p.brand, p.ean, p.mpn, i.id AS imgid, i.url, i.main, c.id AS comid, c.nick, c.body, c.start, t.id AS tagid, t.name AS tagname "
                    + "FROM product p "
                    + "LEFT JOIN product_imgs pi ON p.id = pi.product_id "
                    + "LEFT JOIN img i ON i.id = pi.img_id "
                    + "LEFT JOIN comment c ON p.id = c.prod_id "
                    + "LEFT JOIN product_tags pt ON p.id = pt.product_id "
                    + "LEFT JOIN tag t ON t.id = pt.tag_id WHERE p.id =" + id;

            jdbcTemplate.query(query, (ResultSet rs, int i) -> {
                if (i == 0) {
                    product.setId(rs.getInt("id")).setMark(rs.getShort("mark")).setDiscount(rs.getShort("discount")).setPrice(rs.getFloat("price"))
                            .setPrevPrice(rs.getFloat("prev_price")).setWeight(rs.getFloat("weight")).setWidth(rs.getFloat("width")).setHeight(rs.getFloat("height"))
                            .setVolume(rs.getFloat("volume")).setIsExist(rs.getBoolean("exist")).setName(rs.getString("prname")).setDescription(rs.getString("prdesc"))
                            .setBrand(rs.getString("brand")).setEan(rs.getString("ean")).setMpn(rs.getString("mpn"));
                }

                Comment comment = new Comment(rs.getInt("comid"), rs.getString("body"), rs.getString("nick"), rs.getDate("start"));
                if (comment.id != null && !comments.contains(comment) ) {
                    comments.add(comment);
                }

                Img img = new Img(rs.getInt("imgid"), rs.getString("url"), rs.getBoolean("main"));
                if (img.id != null && !imgs.contains(img) ) {
                    imgs.add(img);
                }

                Tag tag = new Tag(rs.getInt("tagid"), rs.getString("tagname"));
                if (tag.id != null && !tags.contains(tag)) {
                    tags.add(tag);
                }

                return null;
            });
            
            if (!imgs.isEmpty()) {
                product.imgs = imgs;
            }
            
            if (!tags.isEmpty()) {
                product.tags = tags;
            }
            
            if (!comments.isEmpty()) {
                product.comments = comments;
            }

            return product;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Product selectProductCategoriesImgsCommentsTags(Integer id) {
        if (id == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }

        try {
            List<Img> imgs = new ArrayList<>();
            List<Comment> comments = new ArrayList<>();
            List<Tag> tags = new ArrayList<>();
            List<Category> categories = new ArrayList<>();
            Product product = new Product();
            String query = "SELECT DISTINCT p.id, p.mark, p.mark_count, p.start AS prstart, p.last, p.discount, p.price, p.prev_price, p.weight, p.length, p.width, p.height, "
                    + "p.volume, p.exist, p.name AS prname, p.description AS prdesc, p.brand, p.ean, p.mpn, i.id AS iid, i.url, i.main, c.id AS comid, c.nick, c.body, c.start, t.id, "
                    + "t.name AS tagname, cat.name AS catname, cat.id AS catid, cat.pid, cat.leaf, cat.description AS catdesc FROM product p "
                    + "LEFT JOIN product_imgs pi ON p.id = pi.product_id "
                    + "LEFT JOIN img i ON i.id = pi.img_id "
                    + "LEFT JOIN comment c ON p.id = c.prod_id "
                    + "LEFT JOIN product_tags pt ON p.id = pt.product_id "
                    + "LEFT JOIN tag t ON t.id = pt.tag_id "
                    + "LEFT JOIN category_products cp ON p.id = cp.product_id "
                    + "LEFT JOIN category cat ON cat.id = cp.category_id WHERE p.id =" + id;

            jdbcTemplate.query(query, (ResultSet rs, int i) -> {
                if (i == 0) {
                    product.setId(rs.getInt("id")).setMarkCount(rs.getInt("mark_count")).setStart(rs.getDate("prstart")).setLast(rs.getDate("last")).setMark(rs.getShort("mark"))
                            .setDiscount(rs.getShort("discount")).setPrice(rs.getFloat("price")).setPrevPrice(rs.getFloat("prev_price")).setWeight(rs.getFloat("weight"))
                            .setWidth(rs.getFloat("width")).setHeight(rs.getFloat("height")).setVolume(rs.getFloat("volume")).setIsExist(rs.getBoolean("exist"))
                            .setName(rs.getString("prname")).setDescription(rs.getString("prdesc")).setBrand(rs.getString("brand")).setEan(rs.getString("ean"))
                            .setMpn(rs.getString("mpn"));
                }

                Comment comment = new Comment(rs.getInt("comid"), rs.getString("nick"), rs.getString("body"), rs.getDate("start"));
                if (!comments.contains(comment)) {
                    comments.add(comment);
                }

                Img img = new Img(rs.getInt("iid"), rs.getString("url"), rs.getBoolean("main"));
                if (!imgs.contains(img)) {
                    imgs.add(img);
                }

                Tag tag = new Tag(rs.getInt("id"), rs.getString("tagname"));
                if (!tags.contains(tag)) {
                    tags.add(tag);
                }
                
                Category category = new Category(rs.getInt("id"), rs.getInt("pid"), rs.getString("catname"), rs.getString("catdesc"), rs.getBoolean("leaf"));
                if (!categories.contains(category)) {
                    categories.add(category);
                }

                return null;
            });

            return product;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Product> selectListProducts(String query) {
        if (query == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> new Product(rs.getInt("id"), rs.getShort("mark"), rs.getShort("discount"), rs.getFloat("price"), 
                    rs.getFloat("prev_price"), rs.getBoolean("exist"), rs.getString("title"),new Img(rs.getString("url"))));
        } catch (DataAccessException ex) {
            throw new AdminException(ex);
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
    
    @Override @NotNull
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Product> selectRecommended(Integer limit) {
        if (limit == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
            return jdbcTemplate.query("SELECT p.id, p.mark, p.title, i.url FROM " + PGMeta.PRODUCT_TABLE + " p LEFT JOIN  " + PGMeta.PRODUCT_IMGS_TABLE + "  pi ON p.id = pi.product_id "
                    + "LEFT JOIN " + PGMeta.IMG_TABLE + "   i ON  i.id = pi.img_id AND i.main = 'T' WHERE  p.recommend = 'T' LIMIT "+ limit , (ResultSet rs, int i) -> 
                            new Product(rs.getInt("id"), rs.getShort("mark"), rs.getString("title"), new Img(rs.getString("url"))));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override @NotNull
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Product> searchFormSelection(String query) {
        if (query == null) {
            throw new AdminException().addMessage("Controller validation args error.").addExceptionName("IllegalArgumentException");
        }
        
        try {
            return jdbcTemplate.query(query, (ResultSet rs, int i) -> new Product().setId(rs.getInt("id")).setMpn(rs.getString("mpn")).setEan(rs.getString("ean"))
                    .setTitle(rs.getString("title")).setQuantity(rs.getInt("quant")));
        } catch (DataAccessException | IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Product> singleStrArgListProdRet(String query) {
        if (query == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
        return jdbcTemplate.query(query, (ResultSet rs, int i) -> getFullProduct(rs));
        } catch (DataAccessException ex) {
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
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean updateProductCategories(Integer id, Integer oldCategoryId, Integer newCategoryId) {
        if (id == null || oldCategoryId == null || newCategoryId == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
            return jdbcTemplate.update("UPDATE category_products SET category_id = " + newCategoryId +", product_id = " + id + "WHERE category_id =" + oldCategoryId +  ";") != 0;
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean isProdNameExist(String prodName) {
        if (prodName == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
            return jdbcTemplate.query("SELECT EXISTS (SELECT true FROM product WHERE name = ?);", new Object [] { prodName },  (ResultSet rs, int i) 
                    -> rs.getBoolean("exists")).stream().findFirst().orElse(false);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
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

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void insertProduct(InsertProdDto dto) {
        if (!ValidationUtil.validateNull(dto, dto.price, dto.quantity, dto.isExist, dto.isRecommend, dto.name, dto.title, dto.description, dto.categoryIds, dto.mainImg) 
                || dto.tag == null && dto.tagIds == null || dto.tagIds.isEmpty()) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        try {
            if (jdbcTemplate.query("SELECT id FROM product WHERE name =?", new Object [] { dto.name }, (ResultSet rs, int i) -> rs.getInt("id")).stream().findFirst().orElse(null) != null) {
                throw new AdminException().addMessage("Not unique name.");
            }
            
            Integer prodId;
            Integer imgId;
            Integer tagId = null;
            List<Integer> imgIds = new ArrayList<>();
            
            imgId = jdbcTemplate.query("INSERT INTO " + PGMeta.IMG_TABLE + "(url, main)VALUES(?, 'True') RETURNING id", new Object[] { dto.mainImgUrl }, (ResultSet rs, int i) 
                        -> rs.getInt("id")).stream().findFirst().orElse(null);
            
            if (dto.tag != null) {
                tagId = jdbcTemplate.query("INSERT INTO " + PGMeta.TAG_TABLE + "(name)VALUES(?) RETURNING id", new Object[] { dto.tag }, (ResultSet rs, int i) 
                        -> rs.getInt("id")).stream().findFirst().orElse(null);
            }
            
            if (tagId != null) {
                dto.tagIds.add(tagId);
            }
            
            if (imgId != null) {
                imgIds.add(imgId);
            }
            
            if (dto.additionImgUrls != null && dto.additionImgUrls.length > 0) {
                for (String url : dto.additionImgUrls) {
                    Integer tmpImgId = jdbcTemplate.query("INSERT INTO " + PGMeta.IMG_TABLE + "(url)VALUES(?) RETURNING id", new Object[] { url }, (ResultSet rs, int i) 
                        -> rs.getInt("id")).stream().findFirst().orElse(null);
                    if (tmpImgId != null) {
                        imgIds.add(tmpImgId);
                    }
                }
            }
            
            if ((dto.volume == null || dto.volume == 0) && dto.length != null && dto.width != null && dto.height != null) {
                dto.volume = dto.length * dto.width * dto.height; 
            } 
            
            String query = "INSERT INTO " + PGMeta.PRODUCT_TABLE + " (name, title, description, brand, ean, mpn, price, quant, exist, recommend, discount, "
                    + "weight, height, width, length, volume)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";
            
            prodId = jdbcTemplate.query(query, new Object[] {dto.name, dto.title, dto.description, dto.brand, dto.ean, dto.mpn, dto.price, dto.quantity, dto.isExist, dto.isRecommend, dto.discount, 
                dto.weight, dto.height, dto.width, dto.length, dto.volume }, (ResultSet rs, int i) -> rs.getInt("id")).stream().findFirst().orElse(null);
            
            if (prodId == null) {
                throw new AdminException().addMessage("Product not added. Undefind cause.");
            }

            jdbcTemplate.batchUpdate("INSERT INTO " + PGMeta.PRODUCT_IMGS_TABLE +"(img_id, product_id)VALUES" + SqlUtil.getMultyInsertValues(imgIds, prodId), 
                    "INSERT INTO " + PGMeta.PRODUCT_TAGS_TABLE +"(tag_id, product_id)VALUES" + SqlUtil.getMultyInsertValues(dto.tagIds, prodId), 
                    "INSERT INTO " + PGMeta.CATEGORY_PRODUCTS_TABLE +" (category_id,product_id)VALUES" + SqlUtil.getMultyInsertValues(dto.categoryIds, prodId));
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            throw new AdminException(ex);
        } 
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteProduct(Integer id) {
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
    
    private Product getFullProduct(ResultSet rs) throws SQLException {
                            return new Product().setId(rs.getInt("id")).setMarkCount(rs.getInt("mark_count")).setStart(rs.getDate("prstart")).setLast(rs.getDate("last"))
                            .setDiscount(rs.getShort("discount")).setPrice(rs.getFloat("price")).setPrevPrice(rs.getFloat("prev_price")).setWeight(rs.getFloat("weight"))
                            .setWidth(rs.getFloat("width")).setHeight(rs.getFloat("height")).setVolume(rs.getFloat("volume")).setIsExist(rs.getBoolean("exist"))
                            .setName(rs.getString("prname")).setDescription(rs.getString("prdesc")).setBrand(rs.getString("brand")).setEan(rs.getString("ean"))
                            .setMpn(rs.getString("mpn")).setMark(rs.getShort("mark"));
    }
    
    private Product getMinProduct(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("id"), rs.getShort("mark"), rs.getString("title"), new Img(rs.getString("url")));
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
