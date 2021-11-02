
package com.egg.libreria2.services;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.Autor;
import com.egg.libreria2.entidades.Editorial;
import com.egg.libreria2.entidades.Libro;
import com.egg.libreria2.repositorios.AutorRepositorio;
import com.egg.libreria2.repositorios.LibroRepositorio;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibroService{
    
    @Autowired
    private LibroRepositorio libroRepositorio;
    
    @Autowired
    private AutorService autorService;
    
    @Autowired
    private EditorialService editorialService;
    
    @Transactional
    public void AgregarLibro(String id,Long isbn, String titulo,Integer anio,Integer ejemplares,Integer ejemplaresPrestados,Integer ejemplaresRestantes,Autor autor,Editorial editorial) throws ErrorLibro {

        if (titulo == null) {
            throw new ErrorLibro("El titulo no puede ser nulo");
        }
        
        if (anio == null) {
            throw new ErrorLibro("El año no puede ser nulo");
        }
        
        if (ejemplares == null) {
            throw new ErrorLibro("Los ejemplares no pueden ser nulos");
        }
        
        if (ejemplaresPrestados == null) {
            throw new ErrorLibro("Los ejemplares prestados no pueden ser nulos");
        }
        
        if(ejemplaresRestantes==null){
            throw new ErrorLibro("Los ejemplares restantes no pueden ser nulos");
        }
        
        if (autor == null){
            throw new ErrorLibro("El autor no puede ser nulo");
        }
        
        if(editorial == null){
            throw new ErrorLibro("La editorial no puede ser nula");
        }
        
        Libro libro=new Libro();
        libro.setAlta(true);
        libro.setTitulo(titulo);
        libro.setAnio(anio);
        libro.setAutor(autor);
        libro.setEjemplares(ejemplares);
        libro.setEjemplaresPrestados(ejemplaresPrestados);
        libro.setEjemplaresRestantes(ejemplaresRestantes);
        libro.setEditorial(editorial);
        libro.setIsbn(isbn);
        

        libroRepositorio.save(libro);

    }
    
    @Transactional
    public Libro Save(Libro libro) throws ErrorLibro{
        if (libro.getIsbn()==null){
            throw new ErrorLibro("El isbn no puede ser nulo");
        }
        
        if (libro.getTitulo()==null){
            throw new ErrorLibro("El titulo no puede ser nulo");
        }
        
        if (libro.getEjemplares()==null){
            throw new ErrorLibro("Los ejemplares no pueden ser nulos");
        }
        
        if (libro.getEjemplaresRestantes()==null){
            throw new ErrorLibro("Los ejemplares restantes no pueden ser nulos");
        }
        
        if (libro.getEjemplaresPrestados()==null){
            throw new ErrorLibro("Los ejemplares prestados no pueden ser nulos");
        }
        
        if (libro.getAnio()==null){
            throw new ErrorLibro("El año no puede ser nulo");
        }
        
        if (libro.getAutor()==null){
            throw new ErrorLibro("El autor no puede ser nulo");
        }else{
            libro.setAutor(autorService.buscarPorNombre(libro.getAutor().getNombre()));
        }
        
        if (libro.getEditorial()==null){
            throw new ErrorLibro("la editorial no puede ser nula");
        }else{
            libro.setEditorial(editorialService.buscarPorNombre(libro.getEditorial().getNombre()));
        }
        
        libro.setAlta(true);
        
        return libroRepositorio.save(libro);
    }
    
    public void bajaOAltaEditorial(String id) throws ErrorLibro{
        Libro libro=libroRepositorio.findById(id).get();
        Optional<Libro> respuesta= libroRepositorio.findById(id);
        
        if (respuesta.isPresent()){
            libro.setAlta(!libro.getAlta());
        }else{
            throw new ErrorLibro("Ingrese un id correcto");
        }
    }
    
    @Transactional
    public void guardarAutor(Libro libro) throws ErrorLibro{
        if (libro.getAutor() == null){
            throw new ErrorLibro("El autor no puede ser nulo");
        }else{
            libro.setAutor( autorService.guardarAutor(libro.getAutor()));
        }
    }
    
    public void eliminarLibro(String id) throws ErrorLibro {
        Libro libro = libroRepositorio.findById(id).get();
        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            libroRepositorio.delete(libro);
        } else {
            throw new ErrorLibro("Ingrese un id correcto");
        }
    }
    
    public List<Libro> listAll(){
        return libroRepositorio.findAll();
    }
    
    public Optional<Libro> findById(String id){
        return libroRepositorio.findById(id);
    }
}

