package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.service.TransacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("transacoes", transacaoService.listarTodas());
        return "transacoes/lista";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        transacaoService.excluir(id);
        ra.addFlashAttribute("sucesso", "Registro excluido do historico.");
        return "redirect:/transacoes";
    }
}
