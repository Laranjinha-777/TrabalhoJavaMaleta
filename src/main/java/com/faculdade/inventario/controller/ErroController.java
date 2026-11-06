package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.service.RegraNegocioException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErroController {

    @ExceptionHandler(RegraNegocioException.class)
    public String regraNegocio(RegraNegocioException e, Model model) {
        model.addAttribute("mensagem", e.getMessage());
        return "erro";
    }
}
