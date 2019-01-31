package alex.home.angular.controller;

import alex.home.angular.dao.CategoryDao;
import alex.home.angular.dao.PGDao;
import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Category;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ProductCategories;
import alex.home.angular.dto.ProductField;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.dto.SearchQuery;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import alex.home.angular.sql.cache.CategoryCache;
import alex.home.angular.sql.query.QueryFactory;
import alex.home.angular.sql.query.SqlQuery;
import alex.home.angular.sql.search.SearchProductCondition;
import alex.home.angular.utils.img.write.ImageWriter;
import alex.home.angular.utils.properties.PropCache;
import alex.home.angular.utils.properties.PropFsLoader;
import alex.home.angular.utils.properties.PropLoader;
import alex.home.angular.utils.properties.PropLoaderTask;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import alex.home.angular.sql.search.SearchCondition;
import alex.home.angular.sql.search.SearchElement;

@Controller
public class ProductController {
    
    private final PropCache propCache = new PropCache();
    private final CategoryCache categoryCache = new CategoryCache();
    
    private volatile int productCacheVal = -1;
       
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private PGDao pGDao;
    private HttpServletResponse hsr;
    private ImageWriter fsImageWriter;
    private TaskExecutor taskExecutor;

    @PostMapping("/admin/updateProductField")
    @ResponseBody public ResponseRsWrapper updateProductField(@RequestBody ProductField productField, ResponseRsWrapper rrw) {
        if (productField == null || productField.productId == null || productField.columnName == null || productField.value == null ) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalAttributeException")
                    .addMessage("@RequestBody ProductField productField == null || productField.productId == null "
                            + "|| productField.columnName == null || productField.value == null").get()).addHttpErrorStatus(hsr, 500);
        }
        
        try {
            switch (productField.columnName) {
                case "name": productDao.updateProductName(productField.productId, productField.value); break;
                case "description": productDao.updateProductDesc(productField.productId, productField.value); break;
                case "price": productDao.updateProductPrice(productField.productId, Float.parseFloat(productField.value)); break;
                case "mark": productDao.updateProductMark(productField.productId, Integer.parseInt(productField.value)); break;
                case "quantity": productDao.updateProductQuant(productField.productId, Integer.parseInt(productField.value)); break;
            }
            
            return null;
        } catch (ClassCastException | AdminException ex) {
            ex.printStackTrace();
            if (ex.getClass().equals(AdminException.class)) {
                return rrw.addResponse( ((AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
            }
            
            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 400);
        }  
    }
        
    @GetMapping("/admin/product/{id}")
    @ResponseBody public ResponseRsWrapper getProduct(@PathVariable Long id, ResponseRsWrapper rrw) {
        if (id == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalAttributeException").addMessage("@PathVariable Long id == null").get())
                    .addHttpErrorStatus(hsr, 500);
        }
        
        try {
            Product product = productDao.selectProductCategoriesComments(id);
            List<Category> categories = null;
            
            if (product != null) {
                if (categoryCache.isValid()) {
                  categories = categoryCache.getCategiries();
                  
                } else {
                  categories = categoryDao.selectAllCategories();
                  categoryCache.init(categories);
                }
                
                return rrw.addResponse(new ProductCategories(product, categories));
            } else {
                return rrw.addResponse("Ни один товар не соответствует заданному условию.");
            }
            
        } catch (AdminException ex) { 
            if (ex.getClass().equals(AdminException.class)) {
                return rrw.addResponse(ex);
            }
            
            return rrw.addResponse(new AdminException(ex)).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody public ResponseRsWrapper addProduct(@ModelAttribute InsertProdDto dto, ResponseRsWrapper rrw) {
        if (dto == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").addMessage("@ModelAttribute InsertProdDto dto == NULL.").get())
                    .addHttpErrorStatus(hsr, 400);
        }

        try {
            if (dto.image == null  || (dto.image != null && null == (dto.url = fsImageWriter.writeImageAndGetUrl(dto.image.getBytes(), null, null, null)))) {
                dto.url = "/img/no_photo.png";
            }

            productDao.insertProduct(dto);
            return rrw.addResponse("Товар добавлен.");
        } catch (IOException | AdminException ex) {
            ex.printStackTrace();
            if (ex.getClass().equals(AdminException.class)) {
                return rrw.addResponse(ex).addHttpErrorStatus(hsr, 500);
            }

            return rrw.addResponse(new AdminException(ex)).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping(value = "/searchForm", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody public ResponseRsWrapper getSearchForm(ResponseRsWrapper rrw) {
        Properties props = null;
        List<SearchElement> rows = null;
        FutureTask futureTask = null;
        SearchCondition tr = new SearchProductCondition();
        
        try {
          props = propCache.getProductProps(productCacheVal);
          
          if (props == null) {
                PropLoaderTask propTask = new PropLoaderTask(new PropFsLoader(),
                        "/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/prop/db_prod_col.properties");
                futureTask = new FutureTask(propTask);

                taskExecutor.execute(futureTask);
                
                rows = tr.getCondition(pGDao.selectPGFieldMeta("product"));
                props = (Properties) futureTask.get();
                
                if (props != null) {
                    productCacheVal = props.hashCode();
                    
                    propCache.initProdPropsCache(props);
                }
                
            } else {
              rows = tr.getCondition(pGDao.selectPGFieldMeta(PGMeta.PRODUCT_TABLE));
          }
          
          if (rows == null) {
              return rrw.addHttpErrorStatus(hsr, 500).addResponse(new AdminException().addMessage("~UnexpectedResult")
                      .addMessage("rows = tr.getCondition(pGDao.selectPGFieldMeta(PGMeta.PRODUCT_TABLE)); == null").get());
          }

            if (props != null) {
                for (int i = 0; i < rows.size(); i++) {
                    switch (rows.get(i).name) {
                        case "name": rows.get(i).name = props.getProperty("name"); break;
                        case "description": rows.get(i).name = props.getProperty("description"); break;
                        case "price": rows.get(i).name = props.getProperty("price"); break;
                        case "mark":rows.get(i).name = props.getProperty("mark"); break;
                        case "buystat": rows.get(i).name = props.getProperty("buystat"); break;
                        case "start": rows.get(i).name = props.getProperty("start"); break;
                        case "last": rows.get(i).name = props.getProperty("last"); break;
                        case "quant": rows.get(i).name = props.getProperty("quant"); break; 
                        case "exist": rows.get(i).name = props.getProperty("exist"); break;
                    }
                }
            }
            
            return rrw.addResponse(rows);
        } catch (InterruptedException | ExecutionException | AdminException ex) {
            if (ex.getClass().equals(AdminException.class)) {
                rrw.addResponse( ((AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
            }
            return rrw.addResponse(new AdminException(ex))
                    .addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping(value = "/searchQuery")
    @ResponseBody public ResponseRsWrapper getSearchQuery(@RequestBody SearchQuery query, ResponseRsWrapper rrw) {
        if (query == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")
                    .addMessage("@RequestBody SearchQuery query == NULL. Связывание на уровне контроллера не сработало.").get())
                    .addHttpErrorStatus(hsr, 500);
        }
        
        Properties props = null;
        
        try {
            props = propCache.getProductProps(productCacheVal);
            
            if (props == null) {
                PropLoader propLoader = new PropFsLoader();
                props = propLoader.load("/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/prop/db_prod_col.properties");
                
                if (props == null) {
                    return rrw.addHttpErrorStatus(hsr, 500).addResponse(new AdminException().addMessage("/home/alexandr/NetBeansProjects/angular/src/main"
                            + "/webapp/WEB-INF/view/prop/db_prod_col.properties").addExceptionName("FileNotFoundException"));
                            
                }
                
                productCacheVal = props.hashCode();
                
                propCache.initProdPropsCache(props);
            }
            
            List keys = Collections.list(props.propertyNames());
            
            for (int i = 0; i < query.searchQuery.size(); i++) {
                for (int j = 0; j < keys.size(); j++) {
                    if (query.searchQuery.get(i).columnName.equals(props.get(keys.get(j)))) {
                        query.searchQuery.get(i).columnName = String.valueOf(keys.get(j));
                    }
                }
            }
            
            SqlQuery sqlQuery = new QueryFactory().getSquelQuery(query, QueryFactory.TABLE.PRODUCT);
            String sqlRow = sqlQuery.getQueryRow();

            if (sqlRow != null) {
                return rrw.addResponse(productDao.searchFormsSelection(sqlRow));
            }

            return rrw.addResponse(new AdminException().addExceptionName("~ParseException").addMessage("sqlQuery.getQueryRow(). Парсинг запроса не возможен.")
                    .get()).addHttpErrorStatus(hsr, 400);
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/deleteProduct/{id}")
    public ResponseRsWrapper deleteProduct(@PathVariable Long id, ResponseRsWrapper rrw) {
        if (id == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentEaxeption").addMessage("@PathVariable Long id == null").get())
                    .addHttpErrorStatus(hsr, 500);
        }
        
        try {
            productDao.deleteProduct(id);
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
        
        return null;
    }
    
    @Autowired
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Autowired
    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Autowired
    public void setHttpServletResponse(HttpServletResponse hsr) {
        this.hsr = hsr;
    }

    @Autowired
    public void setFsImageWriter(ImageWriter fsImageWriter) {
        this.fsImageWriter = fsImageWriter;
    }

    @Autowired
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    @Autowired
    public void setpGDao(PGDao pGDao) {
        this.pGDao = pGDao;
    }
   
}