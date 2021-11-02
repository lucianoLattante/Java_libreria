
package com.egg.libreria2.controller;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.Autor;
import com.egg.libreria2.entidades.Editorial;
import com.egg.libreria2.entidades.Libro;
import com.egg.libreria2.repositorios.LibroRepositorio;
import com.egg.libreria2.services.AutorService;
import com.egg.libreria2.services.EditorialService;
import com.egg.libreria2.services.LibroService;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class PortalController{
    
    @Autowired
    private LibroRepositorio libroRepositorio;
    
    @Autowired
    private LibroService libroService;
    
    @Autowired
    private AutorService autorService;
    
    @Autowired
    private EditorialService editorialService;
    
    @GetMapping("/")
    public String index(ModelMap model){
        model.addAttribute("libros",libroService.listAll());
        
        return "libros.html";
    }
    
    @GetMapping("/nuevoLibro")
    public String registrar(ModelMap model){
        model.addAttribute("autores",autorService.listAll());
        model.addAttribute("editoriales",editorialService.listAll());
        return "nuevoLibro";
    }
    
    @PostMapping("/cargar")
    public String nuevoLibro(Model model,RedirectAttributes redirectAttributes,@RequestParam Long isbn,@RequestParam String titulo,@RequestParam Integer anio,@RequestParam Integer ejemplares,@RequestParam Integer ejemplaresPrestados,@RequestParam Integer ejemplaresRestantes,@RequestParam String nombre,@RequestParam String nombre1){
        
        try {
            libroService.AgregarLibro(null, isbn, titulo, anio, ejemplares, ejemplaresPrestados, ejemplaresRestantes, autorService.buscarPorNombre(nombre), editorialService.buscarPorNombre(nombre1));
        } catch (ErrorLibro ex) {
            model.addAttribute("error",ex.getMessage());
            redirectAttributes.addFlashAttribute("error",ex.getMessage());
        }
        
        return "redirect:/";
    }
    
    @GetMapping("/listaDeAutores")
    public String listaAutores(ModelMap model){
        model.addAttribute("autores",autorService.listAll());
        
        return "lista-autores";
    }
    
    @GetMapping("/nuevoAutor")
    public String autorNuevo(){
        return "nuevoAutor.html";
    }
    
    @PostMapping("/cargarautor")
    public String nuevoAutor(@RequestParam String nombre){
        
        try {
            
            autorService.AgregarAutor(null, nombre);
        } catch (ErrorLibro ex) {
            Logger.getLogger(PortalController.class.getName()).log(Level.SEVERE, null, ex);
            return "nuevoAutor.html";
        }
        
        return "redirect:/listaDeAutores";
    }
    
    @GetMapping("/disponibilidad-libro")
    public String libroDisponibilidad(@RequestParam (required = true) String id) throws ErrorLibro{
        libroService.eliminarLibro(id);
        
        return "redirect:/";
    }
    
    
    @GetMapping("/nuevaEditorial")
    public String nuevaEditorial(){
        return "nuevaEditorial";
    }
    
    @PostMapping("/cargareditorial")
    public String cargarEditorial(@RequestParam String nombre){
        
        try {
            editorialService.AgregarEditorial(null, nombre);
        } catch (ErrorLibro e) {
            e.printStackTrace();
        }
        return "redirect:/lista-editoriales";
    }
    
    @GetMapping("/lista-editoriales")
    public String listaEditoriales(ModelMap model){
        model.addAttribute("editoriales",editorialService.listAll());
        
        return "lista-editoriales";
    }
    
    @GetMapping("/editar-libro")
    public String editarLibro(Model model,@RequestParam (required = false) String id){
        model.addAttribute("autores",autorService.listAll());
        model.addAttribute("editoriales",editorialService.listAll());
        if (id != null){
            Optional<Libro> optional= libroRepositorio.findById(id);
            if (optional.isPresent()){
                model.addAttribute("libro",optional.get());
            }else{
                return "redirect:/";
            }
        }else{
            model.addAttribute("libro",new Libro());
        }
        
        return "editar-libro";
    }
    
    @PostMapping("/save")
    public String saveLibro(Model model,RedirectAttributes redirectAttributes,@ModelAttribute Libro libro){
        try {
            libroService.Save(libro);
            redirectAttributes.addFlashAttribute("success","Libro guardado con exito");
        } catch (ErrorLibro ex) {
            model.addAttribute("error",ex.getMessage());
            return "editar-libro";
        }
        return "redirect:/";
    }
}
