package alex.home.angular.controller;

import alex.home.angular.dao.PGDao;
import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.dto.SearchQuery;
import alex.home.angular.exception.AdminException;
import alex.home.angular.sql.query.QueryFactory;
import alex.home.angular.sql.query.SqlQuery;
import alex.home.angular.sql.search.SearchTableRow;
import alex.home.angular.sql.search.SearchTableRow.SearchElement;
import alex.home.angular.sql.search.TableRow;
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

@Controller
public class ProductController {
    
    private final  PropCache propCache = new PropCache();
    
    private volatile int productCacheVal = -1;
       
    private ProductDao productDao;
    private PGDao pGDao;
    private HttpServletResponse httpServletResponse;
    private ImageWriter fsImageWriter;
    private TaskExecutor taskExecutor;

        
    @GetMapping("/admin/product/{id}")
    @ResponseBody public ResponseRsWrapper getProduct(@PathVariable Long id, ResponseRsWrapper rrw) {
        Product product = productDao.selectProductCategoriesComments(id);
        if (product != null) {
            return rrw.addResponse(product).addResponseMessage("OK");
        }
        return rrw.addResponseMessage("BAD REQUEST").addHttpErrorStatus(httpServletResponse, 404);
    }
    
    @GetMapping("/products/{category}/{offset}")
    @ResponseBody public ResponseRsWrapper getProductsByCategoryPag(@PathVariable Long category, @PathVariable Integer offset) {
        ResponseRsWrapper rrw = new ResponseRsWrapper();
        int limit = 5;
        List<Product> products = productDao.selectProductsWhereCtegoryId(category, limit, offset);
        if (products != null) {
            return rrw.addResponse(products).addResponseMessage("OK");
        }
        return rrw.addResponseMessage("BAD REQUEST").addHttpErrorStatus(httpServletResponse, 404);
    }
    
    @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody public ResponseRsWrapper addProduct(@ModelAttribute InsertProdDto dto, ResponseRsWrapper rrw) {
        if (dto == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")
                    .addMessage("@ModelAttribute InsertProdDto dto == NULL. Связывание на уровне контроллера не сработало."))
                    .addHttpErrorStatus(httpServletResponse, 500);
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
                return rrw.addResponse(ex).addHttpErrorStatus(httpServletResponse, 500);
            }

            return rrw.addResponse(new AdminException(ex)).addHttpErrorStatus(httpServletResponse, 500);
        }
    }
    
    @PostMapping(value = "/searchTable", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody public ResponseRsWrapper getSearchTable(ResponseRsWrapper rrw) {
        Properties props = null;
        List<SearchElement> rows = null;
        FutureTask futureTask = null;
        TableRow tr = new SearchTableRow();
        
        props = propCache.getProductProps(productCacheVal);
        
        try {
          if (props == null) {
                PropLoaderTask propTask = new PropLoaderTask(new PropFsLoader(),
                        "/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/prop/db_prod_col.properties");
                futureTask = new FutureTask(propTask);

                taskExecutor.execute(futureTask);
                
                rows = tr.getRows(pGDao.selectPGFieldMeta("product"));
                props = (Properties) futureTask.get();
                
                if (props != null) {
                    productCacheVal = props.hashCode();
                    
                    propCache.initProdPropsCache(props);
                }
                
            } else {
              rows = tr.getRows(pGDao.selectPGFieldMeta("product"));
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
            ex.printStackTrace();
            if (ex.getClass().equals(AdminException.class)) {
                rrw.addResponse( ((AdminException) ex).callBack()).addHttpErrorStatus(httpServletResponse, 500);
            }
            return rrw.addResponse(new AdminException(ex))
                    .addHttpErrorStatus(httpServletResponse, 500);
        }
    }
    
    @PostMapping(value = "/searchQuery")
    @ResponseBody public ResponseRsWrapper getSearchQuery(@RequestBody SearchQuery query, ResponseRsWrapper rrw) {
        if (query == null) {
            return rrw.addResponse(new AdminException().addExceptionName("IllegalArgumentException")
                    .addMessage("@RequestBody SearchQuery query == NULL. Связывание на уровне контроллера не сработало."))
                    .addHttpErrorStatus(httpServletResponse, 500);
        }
        
        Properties props = null;
        
        try {
            props = propCache.getProductProps(productCacheVal);
            
            if (props == null) {
                PropLoader propLoader = new PropFsLoader();
                props = propLoader.load("/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/prop/db_prod_col.properties");
                
                if (props == null) {
                    return rrw.addResponse("File Not Found: /home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/prop/db_prod_col.properties")
                            .addHttpErrorStatus(httpServletResponse, 500);
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
                return rrw.addResponse(productDao.searchTableSelection(sqlRow));
            }

            return rrw.addResponse("sqlQuery.getQueryRow(). Парсинг запроса не возможен.").addHttpErrorStatus(httpServletResponse, 400);
        } catch (AdminException ex) {
            return rrw.addResponse(ex.callBack()).addHttpErrorStatus(httpServletResponse, 500);
        }
    }
    
    @Autowired
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Autowired
    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
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
