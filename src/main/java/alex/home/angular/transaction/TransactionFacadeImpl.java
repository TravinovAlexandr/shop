package alex.home.angular.transaction;

import alex.home.angular.dao.CartDao;
import alex.home.angular.dao.ProductDao;
import alex.home.angular.dao.ReportDao;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.ClientInfoProductsSum;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductsCount;
import alex.home.angular.dto.SubmitContract;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import alex.home.angular.utils.SqlUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionFacadeImpl implements TransactionFacade {
    
    private ProductDao productDao;
    private CartDao cartDao;
    private ReportDao reportDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Product selectProduct(Integer id) {
         return productDao.selectProduct(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Product selectProductCategoriesComments(Integer id) {
        return productDao.selectProductCategoriesComments(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ProductsCount selectProductsWhereCtegoryId(Integer categoryId, Integer limit, Integer offset) {
        if (categoryId == null || limit == null || offset == null) {
            return null;
        }
        
        List<Product> products = productDao.singleStrArgListProdRet("SELECT p.*, i.url FROM  product p LEFT JOIN img i ON p.img_id =  i.id "
                + "JOIN category_products cp ON cp.product_id = p.id WHERE cp.category_id = "+ categoryId +" LIMIT " + limit+" OFFSET " + offset);
        if (products == null) {
            return null;
        }
        
        Integer count = productDao.getProductCount("SELECT COUNT(p.id) FROM  product p LEFT JOIN img i ON p.img_id =  i.id "
                + "JOIN category_products cp ON cp.product_id = p.id WHERE cp.category_id = " + categoryId);
        return new ProductsCount(products, count);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Product> selectLastAddedInCategory(Integer catId, Integer limit) {
        if (catId == null || limit == null) {
            return null;
        }
        
        return productDao.singleStrArgListProdRet("SELECT p.*, i.url FROM product p LEFT JOIN img i ON p.img_id = i.id JOIN category_products cp ON cp.product_id = p.id "
                + "AND cp.category_id = " + catId +" WHERE p.id IN (SELECT p.id FROM product p JOIN category_products cp ON cp.product_id = p.id AND cp.category_id = " 
                + catId + "ORDER BY id DESC LIMIT 50 ) ORDER BY RANDOM() LIMIT " + limit);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Product> selectLastAddedInAllCategories(Integer limit) {
        if (limit == null) {
            return null;
        }

        return productDao.singleStrArgListProdRet("SELECT p.*, i.url FROM product p LEFT JOIN img i ON p.img_id = i.id WHERE p.id IN ("
                + "SELECT id FROM product ORDER BY id DESC LIMIT 50 ) ORDER BY RANDOM() LIMIT " + limit);
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<Product> checkCartProducts(SubmitContract sc) {
        if (sc == null || sc.products == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation args error.");
        }
        
        List<Integer> prodIds = new ArrayList<>();
        sc.products.forEach(el -> prodIds.add(el.prodId));
        String inValues = SqlUtil.getINNumSequence(prodIds);
        return productDao.singleStrArgListProdRet("SELECT p.*, i.url FROM product p LEFT JOIN img i ON p.img_id = i.id  WHERE p.id IN (" + inValues + ") "
                + "AND 'F' =  ANY (SELECT exist FROM product WHERE id IN (" + inValues + "))");
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Boolean isConfirmed(Integer cartId) {
     return cartDao.singleStrArgBoolRetIsUpdated("UPDATE cart SET stts = CAST('stop' AS status) WHERE id = " + cartId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public boolean incrementProductMark(Integer id) {
        return productDao.incrementProductMark(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean updateProductCategories(Integer id, Integer oldCategoryId, Integer newCategoryId) {
        return productDao.updateProductCategories(id, oldCategoryId, newCategoryId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void insertProduct(InsertProdDto dto) {
        productDao.insertProduct(dto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void deleteProduct(Integer id) {
        productDao.deleteProduct(id);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateProductName(Integer id, String name) {
        productDao.updateSingleField(id, name, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET name = ? WHERE id = ?","IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateProductDesc(Integer id, String desc) {
        productDao.updateSingleField(id, desc, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET description = ? WHERE id = ?", "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public void updateProductPrice(Integer id, Float price) {
        productDao.updateSingleField(id, price, "UPDATE "  + PGMeta.PRODUCT_TABLE +  " SET price = ? WHERE id = ?" , "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateProductQuant(Integer id, Integer quant) {
        productDao.updateSingleField(id, quant, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET quant = ? WHERE id = ?" , "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateProductMark(Integer id, Integer mark) {
        productDao.updateSingleField(id, mark, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET mark = ? WHERE id = ?"  , "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void updateRecommend(Integer prodId) {
        productDao.updateRecommend("UPDATE product SET recommend = NOT recommend WHERE id = " + prodId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public List<ClientInfoProductsSum> submitContract(SubmitContract sc) {
        if (sc == null || sc.cart == null || sc.cart.id == null || sc.products == null || sc.products.isEmpty()) {
            return null;
        }
          
        List<ClientInfoProductsSum> prods = reportDao.selectConfirmationReport(sc.cart.id);
        
        if (prods == null || prods.isEmpty()) {
            return null;
        }
        
        new Thread(() -> cartDao.updateCart(sc.cart));
       
        return prods;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void addProductInCart(Integer cartId, Integer prodId) {
        if (cartId == null || prodId == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        cartDao.vargsStrArgVoidRet("INSERT INTO cart_orders (cart_id, product_id) VALUES(" + cartId + ", " + prodId + ");" , 
                "UPDATE cart SET lastaccess = now() WHERE id = " + cartId);
    }
    
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteProductFromCart(Integer cartId, Integer prodId) {
        if (cartId == null || prodId == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        cartDao.vargsStrArgVoidRet("DELETE FROM cart_orders WHERE product_id = " + prodId + " AND cart_id =  " + cartId + ";",
                "UPDATE cart SET lastaccess = now() WHERE id = " + cartId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void decProductInCart(Integer cartId, Integer prodId) {
        if (cartId == null || prodId == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }

        cartDao.vargsStrArgVoidRet("DELETE FROM cart_orders WHERE cart_id = " + cartId + " AND product_id = " + prodId + " and id = (SELECT id FROM cart_orders WHERE cart_id = " + cartId + " AND product_id = " + prodId + " LIMIT 1)",
                "UPDATE cart SET lastaccess = now() WHERE id = " + cartId);
    }
    
    private static List<Integer> selectRandom(List<Integer> ids, Integer limit) {
        if (ids == null || limit == null) {
            return null;
        }

        int lmt, index;
        lmt = index = ids.size() < limit ? ids.size() : limit;
        Random random;
        List<Integer> tmpIds = new ArrayList<>();
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

    @Autowired
    public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }
    
    

}
