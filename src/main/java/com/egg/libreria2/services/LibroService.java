
package com.egg.libreria2.services;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.Autor;
import com.egg.libreria2.entidades.Editorial;
import com.egg.libreria2.entidades.Libro;
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
        
        validarCantidades(ejemplares, ejemplaresPrestados, ejemplaresRestantes);

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
        
        validarCantidades(libro.getEjemplares(), libro.getEjemplaresPrestados(), libro.getEjemplaresRestantes());

        if (libro.getAnio()==null){
            throw new ErrorLibro("El año no puede ser nulo");
        }
        
        if (libro.getAutor()==null){
            throw new ErrorLibro("El autor no puede ser nulo");
        }else{
            Autor autor = autorService.buscarPorNombre(libro.getAutor().getNombre());
            if (autor == null) throw new ErrorLibro("El autor no existe");
            libro.setAutor(autor);
        }
        
        if (libro.getEditorial()==null){
            throw new ErrorLibro("la editorial no puede ser nula");
        }else{
            Editorial editorial = editorialService.buscarPorNombre(libro.getEditorial().getNombre());
            if (editorial == null) throw new ErrorLibro("La editorial no existe");
            libro.setEditorial(editorial);
        }
        
        libro.setAlta(true);
        
        return libroRepositorio.save(libro);
    }
    
    @Transactional
    public void bajaOAltaEditorial(String id) throws ErrorLibro {
        Libro libro = requireBook(id);
        libro.setAlta(!Boolean.TRUE.equals(libro.getAlta()));
    }

    private Libro requireBook(String id) throws ErrorLibro {
        if (id == null || id.trim().isEmpty()) {
            throw new ErrorLibro("Ingrese un id correcto");
        }
        return libroRepositorio.findById(id).orElseThrow(() -> new ErrorLibro("El libro no existe"));
    }

    private void validarCantidades(Integer total, Integer prestados, Integer restantes) throws ErrorLibro {
        if (total == null || prestados == null || restantes == null
                || total < 0 || prestados < 0 || restantes < 0
                || prestados > total || total - prestados != restantes) {
            throw new ErrorLibro("Las cantidades deben ser no negativas y el total debe coincidir con prestados más restantes");
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
    
    @Transactional
    public void eliminarLibro(String id) throws ErrorLibro {
        libroRepositorio.delete(requireBook(id));
    }

    public List<Libro> listAll(){
        return libroRepositorio.findAll();
    }
    
    public Optional<Libro> findById(String id){
        return libroRepositorio.findById(id);
    }
}
