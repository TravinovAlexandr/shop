package alex.home.shop.conf;

import alex.home.shop.utils.img.write.ImageFSWriter;
import alex.home.shop.utils.img.write.ImageWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@SpringBootApplication(scanBasePackages = "alex.home.shop")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    
    @Bean
    public ImageWriter fsImageWriter() {
        ImageWriter ifsw = new ImageFSWriter();
        ifsw.setRootImgDir("/home/alexandr/NetBeansProjects/shop/src/main/webapp/WEB-INF/view/img/products");
        ifsw.setDefExctension(".jpg");
        ifsw.setDefFilesInSubDir(300);
        ifsw.setDefImgSize(600);
        return ifsw;
    }
    
    @Bean
    public MultipartResolver multiparResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
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
