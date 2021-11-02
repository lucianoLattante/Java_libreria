
package com.egg.libreria2.controller;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.entidades.Editorial;
import com.egg.libreria2.repositorios.EditorialRepositorio;
import com.egg.libreria2.services.EditorialService;
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
@RequestMapping("/lista-editoriales")
public class EditorialController {
    
    @Autowired
    private EditorialService editorialService;
    
    @Autowired
    private EditorialRepositorio editorialRepositorio;
    
    @GetMapping("/editar-editorial")
    public String editarEditorial(Model model,@RequestParam(required = false) String id){
        if (id != null){
            Optional<Editorial> optional= editorialRepositorio.findById(id);
            if (optional.isPresent()){
                model.addAttribute("editorial",optional.get());
            }else{
                return "redirect:/editar-editorial";
            }
        }else{
            model.addAttribute("editorial",new Editorial());
        }
        
        return "editar-editorial";
    }
    
    @PostMapping("/save-editorial")
    public String saveEditorial(Model model,RedirectAttributes redirectAttributes,@ModelAttribute Editorial editorial){
        try {
            editorialService.guardarEditorial(editorial);
            redirectAttributes.addFlashAttribute("success","Editorial cambiada con exito");
        } catch (ErrorLibro e) {
            e.printStackTrace();
            return "redirect:/lista-editoriales/editar-editorial";
        }
        
        return "redirect:/lista-editoriales";
    }
    
    @GetMapping("/disponibilidad-editorial")
    public String editorialDisponibilidad(@RequestParam (required = true) String id) throws ErrorLibro{
        editorialService.eliminarEditorial(id);
        
        return "redirect:/lista-editoriales";
    }
}
