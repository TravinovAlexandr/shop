package alex.home.angular.conf;

import alex.home.angular.utils.img.write.ImageFSWriter;
import alex.home.angular.utils.img.write.ImageWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@SpringBootApplication(scanBasePackages = "alex.home.angular")
public class AngularApplication {

    public static void main(String[] args) {
        SpringApplication.run(AngularApplication.class, args);
    }
    
    @Bean
    public ImageWriter fsImageWriter() {
        ImageWriter ifsw = new ImageFSWriter();
        ifsw.setRootImgDir("/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/img/products");
        ifsw.setDefExctension(".png");
        ifsw.setDefFilesInSubDir(300);
        return ifsw;
    }
    
    @Bean
    public MultipartResolver multiparResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSizePerFile(997152);
        return multipartResolver;
    }
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }
 }
