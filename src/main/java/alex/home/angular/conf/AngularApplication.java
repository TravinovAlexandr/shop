package alex.home.angular.conf;

import alex.home.angular.utils.img.write.ImageFSWriter;
import alex.home.angular.utils.img.write.ImageWriter;
import javax.servlet.MultipartConfigElement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@SpringBootApplication(scanBasePackages = "alex.home.angular")
public class AngularApplication {

    public static void main(String[] args) {
        SpringApplication.run(AngularApplication.class, args);
    }
    
    @Bean
    public ImageWriter fsImageWriter() {
        ImageWriter ifsw = new ImageFSWriter();
        ifsw.confRootImgDir("/home/alexandr/NetBeansProjects/angular/src/main/webapp/WEB-INF/view/img/products");
        ifsw.confExctention(".jpg");
        ifsw.confSize(300);
        return ifsw;
    }
    
    @Bean
    public MultipartResolver multiparResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSizePerFile(997152);
        return multipartResolver;
    }
 }
