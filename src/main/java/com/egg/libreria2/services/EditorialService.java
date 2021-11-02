package com.egg.libreria2.services;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.Editorial;
import com.egg.libreria2.repositorios.EditorialRepositorio;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditorialService{

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional
    public void AgregarEditorial(String id, String nombre) throws ErrorLibro {

        if (nombre == null) {
            throw new ErrorLibro("El nombre no puede ser nulo");
        }

        Editorial editorial = new Editorial();
        editorial.setAlta(true);
        editorial.setId(id);
        editorial.setNombre(nombre);

        editorialRepositorio.save(editorial);

    }
    
    @Transactional
    public void ModificarEditorial(String id,String nombre) throws ErrorLibro{
        Editorial editorial=editorialRepositorio.findById(id).get();
        Optional<Editorial> respuesta= editorialRepositorio.findById(id);
        
        if (respuesta.isPresent()){
            editorial.setNombre(nombre);
        }else{
            throw new ErrorLibro("Ingrese un id correcto");
        }
    }
    
    public void bajaOAltaEditorial(String id) throws ErrorLibro{
        Editorial editorial=editorialRepositorio.findById(id).get();
        Optional<Editorial> respuesta= editorialRepositorio.findById(id);
        
        if (respuesta.isPresent()){
            editorial.setAlta(!editorial.getAlta());
        }else{
            throw new ErrorLibro("Ingrese un id correcto");
        }
    }
    
    public List<Editorial> listAll(){
        return editorialRepositorio.findAll();
    }
    
    public Editorial buscarPorNombre(String nombre){
        List<Editorial> lista= editorialRepositorio.findAll();
        
        for (Editorial editorial : lista) {
            if (editorial.getNombre().equalsIgnoreCase(nombre)){
                return editorial;
            }
        }
        
        return null;
    }
    
    @Transactional
    public Editorial guardarEditorial(Editorial editorial) throws ErrorLibro{
        if (editorial.getNombre()==null){
            throw new ErrorLibro("El autor no puede ser nulo");
        }
        
        editorial.setAlta(true);
        
        return editorialRepositorio.save(editorial);
    }
    
    public void eliminarEditorial(String id){
        editorialRepositorio.deleteById(id);
    }
}
