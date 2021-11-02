package com.egg.libreria2.services;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.Autor;
import com.egg.libreria2.repositorios.AutorRepositorio;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutorService{

    @Autowired
    private AutorRepositorio autorRepositorio;

    @Transactional
    public void AgregarAutor(String id, String nombre) throws ErrorLibro {

        if (nombre == null) {
            throw new ErrorLibro("El nombre no puede ser nulo");
        }

        Autor autor = new Autor();
        autor.setAlta(true);
        autor.setNombre(nombre);

        autorRepositorio.save(autor);

    }
    
    @Transactional
    public void ModificarAutor(String id, String nombre) throws ErrorLibro {
        Autor autor = autorRepositorio.findById(id).get();
        Optional<Autor> respuesta = autorRepositorio.findById(id);

        if (respuesta.isPresent()) {
            autor.setNombre(nombre);
        } else {
            throw new ErrorLibro("Ingrese un id correcto");
        }
    }

    public void EliminarAutor(String id) throws ErrorLibro {
        Autor autor = autorRepositorio.findById(id).get();
        Optional<Autor> respuesta = autorRepositorio.findById(id);

        if (respuesta.isPresent()) {
            autorRepositorio.delete(autor);
        } else {
            throw new ErrorLibro("Ingrese un id correcto");
        }
    }

    @Transactional
    public Autor guardarAutor(Autor autor) throws ErrorLibro{
        if (autor.getNombre()==null){
            throw new ErrorLibro("El autor no puede ser nulo");
        }
        
        autor.setAlta(true);
        
        return autorRepositorio.save(autor);
    }
    
    public Autor buscarPorNombre(String nombre){
        List<Autor> lista= autorRepositorio.findAll();
        
        for (Autor autor : lista) {
            if (autor.getNombre().equalsIgnoreCase(nombre)){
                return autor;
            }
        }
        
        return null;
    }
    
    public List<Autor> listAll(){
        return autorRepositorio.findAll();
    }

}
