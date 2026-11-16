package com.egg.libreria2;

import com.egg.libreria2.services.UserService;
import javax.servlet.DispatcherType;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_READ = {
        "/", "/listaDeAutores", "/lista-editoriales", "/login", "/registro",
        "/css/**", "/imgs/**", "/js/**"
    };
    private static final String[] ADMIN_FORMS = {
        "/nuevoLibro", "/nuevoAutor", "/nuevaEditorial", "/editar-libro",
        "/lista-autores/editar-autor", "/lista-editoriales/editar-editorial"
    };
    private static final String[] ADMIN_WRITES = {
        "/cargar", "/save", "/cargarautor", "/cargareditorial",
        "/lista-autores/editando-autor", "/lista-editoriales/save-editorial",
        "/disponibilidad-libro", "/lista-autores/disponibilidad-autor",
        "/lista-editoriales/disponibilidad-editorial"
    };

    private final UserService userService;

    public Security(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // Internal error dispatches must be renderable without exposing new public routes.
                .requestMatchers(request -> request.getDispatcherType() == DispatcherType.ERROR).permitAll()
                .antMatchers(HttpMethod.GET, PUBLIC_READ).permitAll()
                .antMatchers(HttpMethod.HEAD, PUBLIC_READ).permitAll()
                .antMatchers(HttpMethod.POST, "/registro").permitAll()
                .antMatchers(HttpMethod.GET, ADMIN_FORMS).hasRole("ADMIN")
                .antMatchers(HttpMethod.HEAD, ADMIN_FORMS).hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, ADMIN_WRITES).hasRole("ADMIN")
                .anyRequest().denyAll()
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
                    .logoutSuccessUrl("/login?logout");
        // Keep Spring Security's default CSRF protection, including login, registration and logout.
    }
}
