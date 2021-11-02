
package com.egg.libreria2.repositorios;

import com.egg.libreria2.entidades.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    
    @Query("SELECT c FROM User c WHERE c.username= :username")
    public User findByUsername(@Param("username") String username);
}
