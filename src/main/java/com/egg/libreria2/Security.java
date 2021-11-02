
package com.egg.libreria2;

import com.egg.libreria2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter{
    // Authethication and authorizations
    
    //UserDetailService -> loadByUserName -> UserService
    
    @Autowired
    private UserService userService;
    
    // Metodo que va a configurar la autenticacion
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }
    
    // Configuraciones de las peticiones http

    //Este metodo para configurar spring security para que no te pida la contraseña el tomcat ese
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().antMatchers("/*").permitAll();
//    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/*","/imgs/*","/js/*").permitAll()
                .and().formLogin()
                      .loginPage("/login")
                      .usernameParameter("username")
                      .passwordParameter("password")
                      .defaultSuccessUrl("/")
                      .loginProcessingUrl("/logincheck")
                      .failureUrl("/login?error=error")
                      .permitAll()
                .and().logout()
                      .logoutUrl("/logout")
                      .logoutSuccessUrl("/login?logout")
                .and().csrf().disable();
    }
    
    
}
