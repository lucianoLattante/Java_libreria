
package com.egg.libreria2.repositorios;

import com.egg.libreria2.entidades.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorialRepositorio extends JpaRepository<Editorial, String> {
    
    @Query("SELECT c FROM Editorial c WHERE c.id= :id")
    public Editorial buscarPorId(@Param("id") String id);
    
}
