package alex.home.angular.transaction;

import alex.home.angular.dao.CartDao;
import alex.home.angular.dao.CategoryDao;
import alex.home.angular.dao.CommentDao;
import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Comment;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductsCount;
import alex.home.angular.dto.SubmitContract;
import alex.home.angular.dto.SubmitContract.ProductInCart;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import alex.home.angular.utils.SqlUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionFacadeImpl implements TransactionFacade {
    
    private ProductDao productDao;
    private CartDao cartDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Product selectProduct(Long id) {
         return productDao.selectProduct(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Product selectProductCategoriesComments(Long id) {
        return productDao.selectProductCategoriesComments(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ProductsCount selectProductsWhereCtegoryId(Long categoryId, Integer limit, Integer offset) {
        if (categoryId == null || limit == null || offset == null) {
            return null;
        }
        
        List<Product> products = productDao.singleStrArgListProdRet("SELECT p.*, i.url FROM  product p JOIN img i ON p.img_id =  i.id "
                + "JOIN category_products cp ON cp.product_id = p.id WHERE cp.category_id = "+ categoryId +" LIMIT " + limit+" OFFSET " + offset);
        if (products == null) {
            return null;
        }
        
        Integer count = productDao.getProductCount("SELECT COUNT(p.id) FROM  product p JOIN img i ON p.img_id =  i.id "
                + "JOIN category_products cp ON cp.product_id = p.id WHERE cp.category_id = " + categoryId);
        return new ProductsCount(products, count);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Product> selectLastAddedInCategory(Long catId, Integer limit) {
        if (catId == null || limit == null) {
            return null;
        }
        
        List<Long> ids = selectRandom(productDao.selectIds("SELECT p.id FROM product p JOIN category_products cp ON cp.product_id = p.id AND cp.category_id = " 
                + catId + " ORDER BY id DESC LIMIT 100"), limit);
        if (ids == null) {
            return null;
        }
        
        String query = "SELECT p.*, i.url FROM product p LEFT JOIN img i ON p.img_id = i.id JOIN category_products cp ON cp.product_id = p.id AND cp.category_id = " + catId 
                +" WHERE p.id IN (" + SqlUtil.getINNumSequence(ids) + " ) ORDER BY RANDOM() LIMIT " + limit;
        return productDao.singleStrArgListProdRet(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Product> selectLastAddedInAllCategories(Integer limit) {
        if (limit == null) {
            return null;
        }
        
        List<Long> ids = selectRandom(productDao.selectIds("SELECT id FROM product ORDER BY id DESC LIMIT 100"), limit);
        if (ids == null) {
            return null;
        }
        
        String query = "SELECT p.*, i.url FROM product p LEFT JOIN img i ON p.img_id = i.id WHERE p.id IN (" +  SqlUtil.getINNumSequence(ids) + ") ORDER BY RANDOM() LIMIT " + limit;
        return productDao.singleStrArgListProdRet(query);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Product> selectRecommend(Integer limit) {
        if (limit == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        return productDao.selectRecommended(limit);
    }
    
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public List<Product> checkCartProducts(SubmitContract sc) {
        if (sc == null || sc.products == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        List<Long> prodIds = new ArrayList<>();
        sc.products.forEach(el -> prodIds.add(el.prodId));
        String inValues = SqlUtil.getINNumSequence(prodIds);
        
        //explain (analyze)with a as (select * from product where id in (30,31,34)) select * from a where not exists (select 'f' in (select exist from a));
        String query = "SELECT * FROM product WHERE id IN (" + inValues + ") AND EXISTS (SELECT 'F' = (SELECT exist FROM product WHERE id IN (" + inValues + ")))";
        return productDao.singleStrArgListProdRet(query);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ProductsCount searchFormSelection(String query) {
        if (query == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }

        List<Product> products = productDao.searchFormSelection(query);
        if (products == null) {
            return null;
        }
        
        ProductsCount pc = new ProductsCount();
        pc.products = products;
        pc.count = productDao.getProductCount(query.substring(0, query.indexOf("LIMIT")).replace("p.*", " COUNT (p.id) "));
        return pc;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Integer getProductCount(String query) {
        return productDao.getProductCount(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public boolean incrementProductMark(Long id) {
        return productDao.incrementProductMark(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean updateProductCategories(Long id, Long oldCategoryId, Long newCategoryId) {
        return productDao.updateProductCategories(id, oldCategoryId, newCategoryId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void insertProduct(InsertProdDto dto) {
        productDao.insertProduct(dto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void deleteProduct(Long id) {
        productDao.deleteProduct(id);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateProductName(Long id, String name) {
        productDao.updateSingleField(id, name, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET name = ? WHERE id = ?","IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateProductDesc(Long id, String desc) {
        productDao.updateSingleField(id, desc, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET description = ? WHERE id = ?", "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateProductPrice(Long id, Float price) {
        productDao.updateSingleField(id, price, "UPDATE "  + PGMeta.PRODUCT_TABLE +  " SET price = ? WHERE id = ?" , "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateProductQuant(Long id, Integer quant) {
        productDao.updateSingleField(id, quant, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET quant = ? WHERE id = ?" , "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateProductMark(Long id, Integer mark) {
        productDao.updateSingleField(id, mark, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET mark = ? WHERE id = ?"  , "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void updateRecommend(Long prodId) {
        productDao.updateRecommend("UPDATE product SET recommend = NOT(SELECT recommend AS rec FROM product WHERE id = "+ prodId +")  WHERE id = " + prodId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void submitContract(SubmitContract sc) {
        if (sc == null || sc.cart == null || sc.cart.cookie == null || sc.products == null || sc.products.isEmpty()) {
            return;
        }
        
        Long id = cartDao.updateCartGetId(sc.cart);
        if (id == -1) {
            return;
        }
        
        cartDao.cartProductsMultyInsertion("INSERT INTO cart_orders VALUES " + SqlUtil.getMultyInsertValues(sc.products, id));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void addProductInCart(Long cartId, Long prodId) {
        if (prodId == null || uuid == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        cartDao.singleStrArgVoidRet("INSERT INTO cart_orders VALUES((SELECT id FROM cart WHERE cookie = '?'), " + prodId + ")", uuid);
    }
    
    
    @Override
    public void deleteProductFromCart(Long cartId, Long prodId) {
        if (prodId == null || uuid == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        //DELETE FROM cart_orders WHERE product_id = product_id and cart_id = (SELECT id FROM cart WHERE cookie = '?')
        cartDao.singleStrArgVoidRet("DELETE FROM cart_orders WHERE product_id = product_id and cart_id = (SELECT id FROM cart WHERE cookie = '?'), " + prodId + ")", uuid);
    }

    @Override
    public void decProductInCart(Long cartId, Long prodId) {
        if (prodId == null || uuid == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        cartDao.singleStrArgVoidRet("INSERT INTO cart_orders VALUES((SELECT id FROM cart WHERE cookie = '?'), " + prodId + ")", uuid);
    }

    @Override
    public void incProductInCart(Long cartId, Long prodId) {
        if (prodId == null || uuid == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        cartDao.singleStrArgVoidRet("INSERT INTO cart_orders VALUES((SELECT id FROM cart WHERE cookie = '?'), " + prodId + ")", uuid);
    }
    
    private static List<Long> selectRandom(List<Long> ids, Integer limit) {
        if (ids == null || limit == null) {
            return null;
        }

        int lmt, index;
        lmt = index = ids.size() < limit ? ids.size() : limit;
        Random random;
        List<Long> tmpIds = new ArrayList<>();
        for (int i = 0; i < lmt; i++) {
            random = new Random();
            int nextEl = index == 0 ? 0 : random.nextInt(index--);
            tmpIds.add(ids.get(nextEl));
            ids.remove(nextEl);
        }
        
        return tmpIds;
    }
    
    @Autowired
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Autowired
    public void setCartDao(CartDao cartDao) {
        this.cartDao = cartDao;
    }


    
    
}
