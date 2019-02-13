package alex.home.angular.controller;

import alex.home.angular.dao.CategoryDao;
import alex.home.angular.dao.ImgDao;
import alex.home.angular.dao.PGDao;
import alex.home.angular.domain.Category;
import alex.home.angular.domain.Img;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.LimitOffset;
import alex.home.angular.dto.ProductCategories;
import alex.home.angular.dto.ProductCategoriesUpdate;
import alex.home.angular.dto.ProductField;
import alex.home.angular.dto.ProductRow;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.dto.SearchQuery;
import alex.home.angular.dto.UpdateProd;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.PGMeta;
import alex.home.angular.sql.cache.CategoryCache;
import alex.home.angular.sql.query.QueryFactory;
import alex.home.angular.sql.query.SqlQuery;
import alex.home.angular.sql.search.SearchCondition;
import alex.home.angular.sql.search.SearchElement;
import alex.home.angular.sql.search.SearchProductCondition;
import alex.home.angular.task.mediator.ImageWriterMediator;
import alex.home.angular.task.mediator.ImageWriterMediator.TLSImageWriterArgs;
import alex.home.angular.task.mediator.ImgDaoMediator;
import alex.home.angular.task.mediator.ImgDaoMediator.ImgDaoArgs;
import alex.home.angular.utils.img.write.ImageWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import alex.home.angular.task.AsyncService;
import alex.home.angular.transaction.TransactionProduct;
import alex.home.angular.utils.properties.PropCache;
import alex.home.angular.utils.properties.PropFsLoader;
import alex.home.angular.utils.properties.PropLoader;
import alex.home.angular.utils.properties.PropLoaderTask;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
  
@RestController
public class ProductController {
    
    private CategoryDao categoryDao;
    private ImgDao imgDao;
    private PGDao pGDao;
    private TransactionProduct transactionProduct;
    private HttpServletResponse hsr;
    private ImageWriter fsImageWriter;
    private TaskExecutor taskExecutor;
    private final AsyncService asyncService = new AsyncService();
    private final PropCache propCache = new PropCache();
    private final CategoryCache categoryCache = new CategoryCache();
    private volatile int productCacheVal = -1;
    
    @PostMapping("/selectLastAddedInCategory/{catId}/{limit}")
    public ResponseRsWrapper selectLastAddedInCategory(@PathVariable Long catId, @PathVariable Integer limit, ResponseRsWrapper rrw) {
        if (limit == null || catId == null) {
            return rrw.addResponse("~ControllerBindingException").addHttpErrorStatus(hsr,400);    
        }
        
        try {
            return rrw.addResponse(transactionProduct.selectLastAddedInCategory(catId, limit));
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr,500);
        } 
    }
    
    @PostMapping("/selectLastAddedInAllCategories/{limit}")
    public ResponseRsWrapper selectLastAddedInAllCategories(@PathVariable Integer limit, ResponseRsWrapper rrw) {
        if (limit == null) {
            return rrw.addResponse("~ControllerBindingException").addHttpErrorStatus(hsr,400);    
        }
        
        try {
            return rrw.addResponse(transactionProduct.selectLastAddedInAllCategories(limit));
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr,500);
        } 
    }
    
    @PostMapping("/getRecommended/{limit}")
    public ResponseRsWrapper getRecommended(@PathVariable Integer limit, ResponseRsWrapper rrw) {
        if (limit == null) {
            return rrw.addResponse("~ControllerBindingException").addHttpErrorStatus(hsr,400);    
        }
        
        try {
            return rrw.addResponse(transactionProduct.selectRecommend(limit));
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr,500);
        }
    }
    
    @PostMapping("/getMainPageProduct/{prodId}")
    public ResponseRsWrapper getMainPageProduct(@PathVariable Long prodId, ResponseRsWrapper rrw) {
        if (prodId == null) {
            return rrw.addResponse("~ControllerBindingException").addHttpErrorStatus(hsr, 400);
        }
        
        try {
            return rrw.addResponse(transactionProduct.selectProductCategoriesComments(prodId));
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr,500);
        }
    }
    
    @PostMapping("/incrementMark/{prodId}")
    public ResponseRsWrapper incrementMark(@PathVariable Long prodId, ResponseRsWrapper rrw) {
        if (prodId == null) {
            return rrw.addResponse("~ControllerBindingException").addHttpErrorStatus(hsr, 400);
        }
        
        try {
            if (!transactionProduct.incrementProductMark(prodId)) {
                return rrw.addHttpErrorStatus(hsr, 500);
            }
            
            return null;
        } catch (AdminException ex) {
            return rrw.addHttpErrorStatus(hsr, 500).addResponse(ex.get());
        }
    }
   
    @PostMapping("/getProductsPage")
    public ResponseRsWrapper getProductsPage(@RequestBody LimitOffset dto, ResponseRsWrapper rrw) {
        if (dto == null || dto.id == null || dto.limit == null | dto.offset == null) {
            return rrw.addResponse("~ControllerBindingException. Check args names.").addHttpErrorStatus(hsr, 400);
        }
        
        try {
            return rrw.addResponse(transactionProduct.selectProductsWhereCtegoryId(dto.id, dto.limit, dto.offset));
         } catch (AdminException ex) {
             return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
         }
    }
    
    @PostMapping("/admin/deleteProduct/{id}")
    public ResponseRsWrapper deleteProduct(@PathVariable Long id, ResponseRsWrapper rrw) {
        if (id == null) {
            return rrw.addResponse(new AdminException().addExceptionName("~ControllerBindingException").get())
                    .addHttpErrorStatus(hsr, 400);
        }
        
        try {
            transactionProduct.deleteProduct(id);
            return null;
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
       
    @PostMapping("/admin/updateCategories")
    public ResponseRsWrapper updateCategories(@RequestBody ProductCategoriesUpdate pcu, ResponseRsWrapper rrw) {
        if (pcu == null || pcu.productId == null || pcu.oldCategoriesId == null || pcu.newCategoriesId == null || (pcu.oldCategoriesId.isEmpty() && pcu.newCategoriesId.isEmpty())) {
            return rrw.addResponse(new AdminException().addMessage("Chech args names").addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
        }
        
        try {
            int oldCategLength = pcu.oldCategoriesId.size();
            int newCategLength = pcu.newCategoriesId.size();
            List<Long> categoreToDelete = new ArrayList<>();
            List<Long> categoreToInsert = new ArrayList<>();
            
            if (oldCategLength == 0 && newCategLength != 0) {
                categoreToInsert.addAll(pcu.newCategoriesId);
            } else if (newCategLength == 0 && oldCategLength != 0) {
                 categoreToDelete.addAll(pcu.oldCategoriesId);
            } else {
                List<Long> tmp = new ArrayList<>(pcu.oldCategoriesId);
                tmp.removeAll(pcu.newCategoriesId);
                categoreToDelete.addAll(tmp);
                tmp = new ArrayList<>(pcu.newCategoriesId);
                tmp.removeAll(pcu.oldCategoriesId);
                categoreToInsert.addAll(tmp);
            }
            
            pcu.oldCategoriesId = categoreToDelete;
            pcu.newCategoriesId = categoreToInsert;

            categoryDao.updateProductCategories(pcu);
            return null;
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping(value = "/admin/updateImg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseRsWrapper updateImg(@ModelAttribute UpdateProd dto, ResponseRsWrapper rrw) {
        if (dto == null || dto.imageId == null || dto.image == null) {
            return rrw.addHttpErrorStatus(hsr, 400).addResponse(new AdminException().addExceptionName("IllegalArgumentException")
                    .addMessage(dto == null ? "@ModelAttribute UpdateProd dto == null" : " " + dto.imageId == null ? "dto.imageId == null" : "  " + dto.image == null ? "dto.image == null" : ""));
        }
        
        try {    
            asyncService.initCall("writeFSMed", new ImageWriterMediator(fsImageWriter, TLSImageWriterArgs.create(dto.image.getBytes(), null, null, null, null)));
            Future writeFSImg = asyncService.execute("writeFSMed", taskExecutor);
            asyncService.initCall("selectMed", new ImgDaoMediator(imgDao, ImgDaoArgs.create("IMG_SELECT", dto.imageId)));
            Future selectMed = asyncService.execute("selectMed", taskExecutor);
            String newImgPath = (String) writeFSImg.get();
            asyncService.initCall("updateMed", new ImgDaoMediator(imgDao, ImgDaoArgs.create("IMG_UPDATE", dto.imageId, newImgPath)));
            Future updateMed = asyncService.execute("updateMed", taskExecutor);
            Img img = (Img) selectMed.get();
            taskExecutor.execute(() -> fsImageWriter.deleteImage(img.url));
            updateMed.get();
            return null;  
        } catch (IOException | InterruptedException | ExecutionException | AdminException ex) {
            ex.printStackTrace();
            if (ex.getClass() == AdminException.class) {
                return rrw.addResponse( ((AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
            }
                
            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping("/admin/updateProductField")
    public ResponseRsWrapper updateProductSingleField(@RequestBody ProductField productField, ResponseRsWrapper rrw) {
        if (productField == null || productField.productId == null || productField.columnName == null || productField.value == null ) {
            
            return rrw.addHttpErrorStatus(hsr, 400).addResponse(new AdminException().addExceptionName("IllegalAttributeException").addMessage("@RequestBody ProductField "
                    + "productField == null || productField.productId == null " + "|| productField.columnName == null || productField.value == null").get());
        }
        
        try {
            switch (productField.columnName) {
                case "name": transactionProduct.updateProductName(productField.productId, productField.value); break;
                case "description": transactionProduct.updateProductDesc(productField.productId, productField.value); break;
                case "price": transactionProduct.updateProductPrice(productField.productId, Float.parseFloat(productField.value)); break;
                case "mark": transactionProduct.updateProductMark(productField.productId, Integer.parseInt(productField.value)); break;
                case "quantity": transactionProduct.updateProductQuant(productField.productId, Integer.parseInt(productField.value)); break;
            }
            
            return null;
        } catch (ClassCastException | AdminException ex) {
            ex.printStackTrace();
            if (ex.getClass() == AdminException.class) {
                return rrw.addResponse( ((AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
            }
            
            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 400);
        }  
    }
    
    @PostMapping("/admin/product/{id}")
    public ResponseRsWrapper getUpdateProductForm(@PathVariable Long id, ResponseRsWrapper rrw) {
        if (id == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalAttributeException").addMessage("Chech atrs names.").get())
                    .addHttpErrorStatus(hsr, 400);
        }
        
        try {
            Product product = transactionProduct.selectProductCategoriesComments(id);
            List<Category> categories;
            
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
            if (ex.getClass() == AdminException.class) {
                return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
            }
            
            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    //подумать о достаточном колве полей для товара
    @PostMapping(value = "/admin/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody public ResponseRsWrapper addProduct(@ModelAttribute InsertProdDto dto, ResponseRsWrapper rrw) {
        if (dto == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").addMessage("Check atrs names.").get())
                    .addHttpErrorStatus(hsr, 400);
        }

        try {
            if (dto.image == null  || null == (dto.url = fsImageWriter.writeImageAndGetUrl(dto.image.getBytes(), null, null, null))) {
                dto.url = "/img/no_photo.png";
            }

            transactionProduct.insertProduct(dto);
            return null;
        } catch (IOException | AdminException ex) {
            ex.printStackTrace();
            if (ex.getClass() == AdminException.class) {
                return rrw.addResponse( ((AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
            }

            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping(value = "/admin/searchForm")
    public ResponseRsWrapper getSearchProductForm(ResponseRsWrapper rrw) {
        Properties props;
        List<SearchElement> rows;
        FutureTask futureTask;
        SearchCondition searcCond = new SearchProductCondition();
        
        try {
          props = propCache.getProductProps(productCacheVal);
          
          if (props == null) {
                PropLoaderTask propTask = new PropLoaderTask(new PropFsLoader(), "/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/prop/db_prod_col.properties");
                futureTask = new FutureTask(propTask);
                taskExecutor.execute(futureTask);
                rows = searcCond.getCondition(pGDao.selectPGFieldMeta(PGMeta.PRODUCT_TABLE));
                props = (Properties) futureTask.get();
                
                if (props != null) {
                    productCacheVal = props.hashCode();
                    propCache.initProdPropsCache(props);
                }
                
            } else {
              rows = searcCond.getCondition(pGDao.selectPGFieldMeta(PGMeta.PRODUCT_TABLE));
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
            if (ex.getClass() == AdminException.class) {
                rrw.addResponse( ((AdminException) ex).get()).addHttpErrorStatus(hsr, 500);
            }
            return rrw.addResponse(new AdminException(ex).get()).addHttpErrorStatus(hsr, 500);
        }
    }
    
    @PostMapping(value = "/admin/searchQuery")
    public ResponseRsWrapper searchQuery(@RequestBody SearchQuery query, ResponseRsWrapper rrw) {
        if (query == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException").addMessage("@RequestBody SearchQuery query == null.").get())
                    .addHttpErrorStatus(hsr, 500);
        }
        
        Properties props;
        
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
                return rrw.addResponse(transactionProduct.searchFormSelection(sqlRow));
            }

            return rrw.addResponse(new AdminException().addExceptionName("~ParseQueryException").addMessage("Qury row == null.").get()).addHttpErrorStatus(hsr, 400);
        } catch (AdminException ex) {
            return rrw.addResponse(ex.get()).addHttpErrorStatus(hsr, 500);
        }
    }
    

    @Autowired
    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Autowired
    public void setImgDao(ImgDao imgDao) {
        this.imgDao = imgDao;
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
    public void setTransactionProduct(TransactionProduct transactionProduct) {
        this.transactionProduct = transactionProduct;
    }

    @Autowired
    public void setpGDao(PGDao pGDao) {
        this.pGDao = pGDao;
    }
    
}
