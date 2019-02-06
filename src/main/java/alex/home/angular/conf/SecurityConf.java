package alex.home.angular.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;


@Configuration
public class SecurityConf extends WebSecurityConfigurerAdapter {

    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder();
//        Pbkdf2PasswordEncoder
    }

//    @Bean
//    public DaoAuthenticationProvider authProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userAuthService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authProvider());
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin()
                .loginPage("/auth")
                .successForwardUrl("/admin")
                .failureUrl("/auth")
                .passwordParameter("password")
                .usernameParameter("nick")
                .and()
                .authorizeRequests()
//                .antMatchers("/admin", "/addProduct").authenticated();
                .antMatchers("/543").authenticated();
    }

//    public void configureGlobal(@Autowired AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userAuthService).passwordEncoder(passwordEncoder());
////            auth.userDetailsService(userAuthService);
//    }


}
