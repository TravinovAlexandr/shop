package alex.home.shop.conf;

import alex.home.shop.filter.CacheFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

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
    
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter cef = new CharacterEncodingFilter("UTF-8");
        cef.setForceRequestEncoding(true);
        cef.setForceResponseEncoding(true);
        return cef;
    }
    
}
