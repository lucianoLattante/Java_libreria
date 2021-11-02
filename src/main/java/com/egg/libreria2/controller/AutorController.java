
package com.egg.libreria2.controller;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.Autor;
import com.egg.libreria2.repositorios.AutorRepositorio;
import com.egg.libreria2.services.AutorService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lista-autores")
public class AutorController {
    
    @Autowired
    private AutorService autorService;
    
    @Autowired
    private AutorRepositorio autorRepositorio;
    
    @GetMapping("/disponibilidad-autor")
    public String autorDisponibilidad(@RequestParam (required = true) String id) throws ErrorLibro{
        autorService.EliminarAutor(id);
        
        return "redirect:/listaDeAutores";
    }
    
    @GetMapping("/editar-autor")
    public String editarAutor(Model model,@RequestParam (required = false)String id){
        if (id != null){
            Optional<Autor> optional= autorRepositorio.findById(id);
            if (optional.isPresent()){
                model.addAttribute("autor",optional.get());
            }else{
                return "redirect:/editar-autor";
            }
        }else{
            model.addAttribute("autor",new Autor());
        }
        return "editar-autor";
    }
    
    @PostMapping("/editando-autor")
    public String autorEditado(Model model,RedirectAttributes redirectAttributes,@ModelAttribute Autor autor){
        try {
            autorService.guardarAutor(autor);
            redirectAttributes.addFlashAttribute("success","Autor guardado con exito");
        } catch (ErrorLibro e) {
            e.printStackTrace();
            return "redirect:/listaDeAutores/editando-autor";
        }
        
        
        return "redirect:/listaDeAutores";
    }
}
