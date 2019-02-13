package alex.home.angular.transaction;

import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductRow;
import alex.home.angular.dto.ProductsCount;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import alex.home.angular.utils.PGUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionProductImpl implements TransactionProduct {
    
    private ProductDao productDao;

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
        
        List<Product> products = productDao.selectProductsWhereCtegoryId("SELECT p.*, i.url FROM  product p JOIN img i ON p.img_id =  i.id "
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
                +" WHERE p.id IN (" + PGUtil.getINNumSequence(ids) + " ) ORDER BY RANDOM() LIMIT " + limit;
        return productDao.selectLastAdded(query);
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
        
        String query = "SELECT p.*, i.url FROM product p LEFT JOIN img i ON p.img_id = i.id WHERE p.id IN (" +  PGUtil.getINNumSequence(ids) + ") ORDER BY RANDOM() LIMIT " + limit;
        return productDao.selectLastAdded(query);
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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ProductRow> searchFormSelection(String query) {
        return productDao.searchFormSelection(query);
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
    
}
