
package com.egg.libreria2.controller;

import com.egg.libreria2.Errores.ErrorLibro;
import com.egg.libreria2.services.UserService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/registro")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("")
    public String registro(){
        return "registro";
    }
    
    @PostMapping("")
    public String registroSave(Model model,@RequestParam String username,@RequestParam String password,@RequestParam String password2){
        
        try {
            userService.save(username, password, password2);
            return "redirect:/";
        } catch (ErrorLibro ex) {
            model.addAttribute("error",ex.getMessage());
            model.addAttribute("username",username);
            return "registro";
        }
    }
    
}
