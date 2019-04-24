package alex.home.shop.transaction;

import alex.home.shop.dao.CartDao;
import alex.home.shop.dao.ProductDao;
import alex.home.shop.dao.ReportDao;
import alex.home.shop.domain.Product;
import alex.home.shop.dto.ClientInfoProductsSum;
import alex.home.shop.dto.InsertProdDto;
import alex.home.shop.dto.ProductsCount;
import alex.home.shop.dto.SubmitContract;
import alex.home.shop.exception.AdminException;
import alex.home.shop.sql.PGMeta;
import alex.home.shop.utils.SqlUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
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
    public Product selectProductImgsCommentsTags(Integer id) {
        return productDao.selectProductImgsCommentsTags(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Product selectProductCategoriesImgsCommentsTags(Integer id) {
        return productDao.selectProductCategoriesImgsCommentsTags(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ProductsCount selectProductsWhereCtegoryId(Integer categoryId, Integer limit, Integer offset) {
        if (categoryId == null || limit == null || offset == null) {
            return null;
        }
        
        List<Product> products = productDao.selectListProducts("SELECT p.*, i.url FROM  product p LEFT JOIN product_imgs pi ON p.id = pi.product_id LEFT JOIN img i ON i.id = pi.img_id "
                + "LEFT JOIN category_products cp ON cp.product_id = p.id WHERE i.main = 'T' AND cp.category_id = "+ categoryId +" LIMIT " + limit+" OFFSET " + offset);
        if (products == null) {
            return null;
        }
        
        Integer count = productDao.getProductCount("SELECT COUNT(p.id) FROM  product p LEFT JOIN product_imgs pi ON p.id = pi.product_id LEFT JOIN img i ON i.id = pi.img_id  "
                + "LEFT JOIN category_products cp ON cp.product_id = p.id WHERE i.main = 'T' AND cp.category_id = " + categoryId);
        return new ProductsCount(products, count);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Product> selectLastAddedInCategory(Integer catId, Integer limit) {
        if (catId == null || limit == null) {
            return null;
        }
        String query = "SELECT p.id, p.mark, p.title, i.url FROM product p "
                + "LEFT JOIN product_imgs pi ON p.id=pi.product_id "
                + "LEFT JOIN img i ON i.id=pi.img_id AND i.main='T' "
                + "LEFT JOIN category_products cp ON p.id = cp.product_id "
                + "LEFT JOIN category c ON c.id=cp.category_id AND cp.category_id =" + catId
                + " WHERE p.id IN (SELECT p.id FROM product p "
                + "JOIN category_products cp ON cp.product_id = p.id AND cp.category_id ="+ catId 
                + "ORDER BY id DESC LIMIT 50 ) ORDER BY RANDOM() LIMIT " + limit;
        
        return productDao.selectMinProducts(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Product> selectLastAddedInAllCategories(Integer limit) {
        if (limit == null) {
            return null;
        }

        return productDao.selectMinProducts("SELECT p.id, p.mark, p.title, i.url FROM product p LEFT JOIN product_imgs pi ON p.id=pi.product_id LEFT JOIN img i ON i.id=pi.img_id "
                + "WHERE p.id IN (SELECT id FROM product ORDER BY id DESC LIMIT 50 ) ORDER BY RANDOM() LIMIT " + limit);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
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
        String query = "SELECT p.id, p.title, p.price, p.prev_price, p.mark, p.exist, p.discount, i.url FROM product p LEFT JOIN product_imgs pi ON p.id = pi.product_id LEFT JOIN img i ON i.id = pi. img_id WHERE p.id IN (" + inValues + ") AND i.main = 'T' AND 'F' =  ANY (SELECT exist FROM product WHERE id IN (" + inValues + "))";
//        return productDao.singleStrArgListProdRet("SELECT p.*, i.url FROM product p LEFT JOIN img i ON p.img_id = i.id  WHERE p.id IN (" + inValues + ") "
//                + "AND 'F' =  ANY (SELECT exist FROM product WHERE id IN (" + inValues + "))");
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
        pc.count = productDao.getProductCount(query.substring(0, query.indexOf("GROUP")).replace("p.*", " COUNT (p.id) "));
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean incrementProductMark(Integer id) {
        return productDao.incrementProductMark(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean updateProductCategories(Integer id, Integer oldCategoryId, Integer newCategoryId) {
        return productDao.updateProductCategories(id, oldCategoryId, newCategoryId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public boolean isProductNameExist(String prodName) {
        return productDao.isProdNameExist(prodName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void insertProduct(InsertProdDto dto) {
        productDao.insertProduct(dto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteProduct(Integer id) {
        productDao.deleteProduct(id);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateProductName(Integer id, String name) {
        productDao.updateSingleField(id, name, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET name = ? WHERE id = ?","IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void updateProductDesc(Integer id, String desc) {
        productDao.updateSingleField(id, desc, "UPDATE " + PGMeta.PRODUCT_TABLE +" SET description = ? WHERE id = ?", "IllegalArgumentException", "Controller validation args error.");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
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
        
        cartDao.updateCart(sc.cart);
       
        return prods;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void addProductInCart(Integer cartId, Integer prodId) {
        if (cartId == null || prodId == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        cartDao.vargsStrArgVoidRet("INSERT INTO cart_products (cart_id, product_id) VALUES(" + cartId + ", " + prodId + ");" , 
                "UPDATE cart SET lastaccess = now() WHERE id = " + cartId);
    }
    
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteProductFromCart(Integer cartId, Integer prodId) {
        if (cartId == null || prodId == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }
        
        cartDao.vargsStrArgVoidRet("DELETE FROM cart_products WHERE product_id = " + prodId + " AND cart_id =  " + cartId + ";",
                "UPDATE cart SET lastaccess = now() WHERE id = " + cartId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void decProductInCart(Integer cartId, Integer prodId) {
        if (cartId == null || prodId == null) {
            throw new AdminException().addExceptionName("IllegalArgumentException").addMessage("Controller validation err");
        }

        cartDao.vargsStrArgVoidRet("DELETE FROM cart_products WHERE cart_id = " + cartId + " AND product_id = " + prodId + " and id = (SELECT id FROM cart_products WHERE cart_id = " + cartId + " AND product_id = " + prodId + " LIMIT 1)",
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
