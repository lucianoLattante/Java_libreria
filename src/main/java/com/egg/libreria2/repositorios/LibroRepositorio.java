package com.egg.libreria2.repositorios;

import com.egg.libreria2.entidades.Autor;
import com.egg.libreria2.entidades.Editorial;
import com.egg.libreria2.entidades.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, String>{
    
    @Query("SELECT c FROM Libro c WHERE c.id= :id")
    public Libro buscarPorId(@Param("id") String id);
    
}
