package alex.home.angular.conf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "alex.home.angular")
public class AngularApplication {

    public static void main(String[] args) {
        SpringApplication.run(AngularApplication.class, args);
    }
}
