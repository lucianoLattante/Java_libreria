
package com.egg.libreria2.services;

import com.egg.libreria2.Enum.Role;
import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.User;
import com.egg.libreria2.repositorios.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService{
 
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public User save(String username,String password,String password2) throws ErrorLibro{
        User user=new User();
        
        if (username==null){
            throw new ErrorLibro("El nombre de usuario no puede ser nulo");
        }
        
        if(password.isEmpty() ||password==null || password2==null || password2.isEmpty()){
            throw new ErrorLibro("La contraseña no puede estar vacia");
        }
        
        user.setUsername(username);
        
        BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
        
        if(password.equals(password2)){
               user.setPassword(encoder.encode(password)); 
            }else{
                throw new ErrorLibro("Las contraseñas deben coincidir");
            }
        
        user.setRol(Role.USER);
        
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        try {
            User user= userRepository.findByUsername(username);
            org.springframework.security.core.userdetails.User usuario;
            
            List<GrantedAuthority> authorities= new ArrayList<>();
            
            authorities.add(new SimpleGrantedAuthority("ROLE_"+user.getRol()));
            
            return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException("El usuario no existe");
        }
    }
}
