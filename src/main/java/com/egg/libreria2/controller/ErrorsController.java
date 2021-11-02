
package com.egg.libreria2.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorsController implements ErrorController {
    
    @RequestMapping(value = "/error",method = {RequestMethod.GET,RequestMethod.POST})
    public String direccionarError(HttpServletRequest http,Model model){
        String mensajeError ="";
        
        int codigoError =(int) http.getAttribute("javax.servlet.error.status_code");
        
        switch (codigoError){
            case 400:
                mensajeError="El recurso solicitado no existe";
                break;
            case 401:
                mensajeError="No se encuentra autorizado para realizar esta peticion";
                break;
            case 403:
                mensajeError="No tiene permisos para acceder al recurso";
                break;
            case 404:
                mensajeError="El recurso solicitado no se ha encontrado";
                break;
            case 500:
                mensajeError = "El servidor no pudo realizar la peticion con exito";
                break;
        }
        
        model.addAttribute("codigo",codigoError);
        model.addAttribute("mensaje",mensajeError);
        
        return "error";
    }
    
}
