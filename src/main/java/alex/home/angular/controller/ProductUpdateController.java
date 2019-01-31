package alex.home.angular.controller;

import alex.home.angular.dao.CategoryDao;
import alex.home.angular.dto.ProductCategoriesUpdate;
import alex.home.angular.dto.ResponseRsWrapper;
import alex.home.angular.dto.UpdateProd;
import alex.home.angular.exception.AdminException;
import alex.home.angular.task.AsyncTask;
import alex.home.angular.utils.img.write.ImageWriter;
import alex.home.angular.utils.img.write.ImageWriter.TLSImageWriterArgs;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductUpdateController {
    
    private CategoryDao categoryDao;
    private HttpServletResponse hsr;
    private ImageWriter fsImageWriter;
    private TaskExecutor taskExecutor;

    
    @PostMapping("/admin/updateCategories")
    public ResponseRsWrapper updateCategories(@RequestBody ProductCategoriesUpdate pcu, ResponseRsWrapper rrw) {
        if (pcu == null || pcu.productId == null || pcu.oldCategoriesId == null || pcu.newCategoriesId == null 
                || (pcu.oldCategoriesId.isEmpty() && pcu.newCategoriesId.isEmpty())) {
            
            return rrw.addResponse(new AdminException().addMessage("@RequestBody ProductCategoriesUpdate pcu == null || pcu.productId == null || pcu.categoriesId == null")
                    .addExceptionName("IllegalArgumentException").get()).addHttpErrorStatus(hsr, 400);
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
        if (dto == null || dto.productId == null || dto.img == null) {
            return null;
        }
        
        try {
            fsImageWriter.initMethodArguments(TLSImageWriterArgs.create(dto.img.getBytes(), null, null, null, null));
            AsyncTask writeImg = new AsyncTask(fsImageWriter);
            
            CompletableFuture cf = new CompletableFuture();
            cf.supplyAsync(() -> writeImg.get());
            
            String s = (String) cf.get();
            
            System.out.println(s);
            
            //1.асинхронно писать картинку
            //2.в это время считать из бд айди картинки
            //3.асинхронно писать картинку в бд
            //4.синхронно удалять картинку из фс
            //5.дождаться 3.
            //6.вернуть ответ пользователю
//            String imgUrl = fsImageWriter.writeImageAndGetUrl(dto.img.getBytes(), null, null, null);
//            
//            String query0 = "SELECT img_id FROM product WHERE id = 1;";
//            
//            String query = "UPDATE img SET url = ? WHERE id = 1213;";
            
          return null;  
        } catch (IOException | InterruptedException | ExecutionException | AdminException ex) {
            ex.printStackTrace();
            return null;
        }
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
    
    
    
}
