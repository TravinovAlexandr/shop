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