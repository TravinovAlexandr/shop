package alex.home.angular.controller;

import alex.home.angular.dao.PGDao;
import alex.home.angular.dao.ProductDao;
import alex.home.angular.domain.Product;
import alex.home.angular.dto.InsertProdDto;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.dto.SearchQuery;

import alex.home.angular.sql.query.QueryFactory;
import alex.home.angular.sql.query.SqlQuery;
import alex.home.angular.sql.query.SqlQueryProduct;
import alex.home.angular.sql.search.SearchTableRow;
import alex.home.angular.sql.search.TableRow;
import alex.home.angular.utils.img.write.ImageWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductController {
    
    private ProductDao productDao;
    private HttpServletResponse httpServletResponse;
    private ImageWriter fsImageWriter;
    private PGDao pGDao;
    
    @GetMapping("/product/{id}")
    @ResponseBody public ResponseRsWrapper getProduct(@PathVariable Long id) {
        ResponseRsWrapper rrw = new ResponseRsWrapper();
        Product product = productDao.selectProductWithImage(id);
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
    
    @PostMapping(value = "/addProduct", consumes = "multipart/form-data")
    @ResponseBody public ResponseRsWrapper addProduct(@ModelAttribute InsertProdDto dto) {
        ResponseRsWrapper rrw = new ResponseRsWrapper();
        if (dto != null) {
            try {
                if (dto.image != null) {
                    String url = fsImageWriter.writeImageAndGetUrl(dto.image.getBytes(), null, null, null);
                    dto.url = url;
                } else {
                    dto.url = "/img/no_photo.png".intern();
                }
                if (productDao.insertProduct(dto)) {
                    return rrw.addResponse("Продукт добавлен.");
                }
                return rrw.addResponse("Продукт не добавлен.").addHttpErrorStatus(httpServletResponse, 500);
            } catch (IOException ex) {
                ex.printStackTrace();
                return rrw.addResponse("Продукт не добавлен.").addHttpErrorStatus(httpServletResponse, 500);
            }
        }
        return rrw.addResponse("Продукт не добавлен.").addHttpErrorStatus(httpServletResponse, 500);
    }
    
    @PostMapping(value = "/getSearchTable", produces ="application/json")
    @ResponseBody public ResponseRsWrapper getSearchTable(ResponseRsWrapper rrw) {
        TableRow tr = new SearchTableRow();
        rrw.addResponse(tr.getRows(pGDao.selectPGFieldMeta("product")));
        return rrw;
    }
    

    @PostMapping(value = "/searchQuery")
    @ResponseBody public String retrieveSearchQuery(@RequestBody SearchQuery queryList) {
        SqlQuery sq = new QueryFactory().getSquelQuery(queryList.searchQuery, QueryFactory.TABLE.PRODUCT);
        String f = sq.getQueryRow();
        return null;
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
    public void setpGDao(PGDao pGDao) {
        this.pGDao = pGDao;
    }
   
}
