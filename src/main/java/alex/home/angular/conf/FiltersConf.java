package alex.home.angular.conf;

import alex.home.angular.filter.CacheFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FiltersConf {
    
    @Bean
    public CacheFilter cacheFilter() {
        return new CacheFilter();
    }

    @Bean
    public FilterRegistrationBean<CacheFilter> loggingFilter(){
        FilterRegistrationBean<CacheFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(cacheFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;    
    }
}
