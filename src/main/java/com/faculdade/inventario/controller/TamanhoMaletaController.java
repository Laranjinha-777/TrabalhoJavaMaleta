package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.TamanhoMaleta;
import com.faculdade.inventario.model.service.RegraNegocioException;
import com.faculdade.inventario.model.service.TamanhoMaletaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tamanhos")
public class TamanhoMaletaController {

    private final TamanhoMaletaService tamanhoService;

    public TamanhoMaletaController(TamanhoMaletaService tamanhoService) {
        this.tamanhoService = tamanhoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tamanhos", tamanhoService.listarTodos());
        return "tamanhos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("tamanho", new TamanhoMaleta());
        return "tamanhos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("tamanho", tamanhoService.buscarPorId(id));
        return "tamanhos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("tamanho") TamanhoMaleta tamanho, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "tamanhos/form";
        }
        try {
            tamanhoService.salvar(tamanho);
        } catch (RuntimeException e) {
            model.addAttribute("erro", "Nao foi possivel salvar: verifique se o nome ja existe.");
            return "tamanhos/form";
        }
        ra.addFlashAttribute("sucesso", "Tamanho \"" + tamanho.getNome() + "\" salvo.");
        return "redirect:/tamanhos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            tamanhoService.excluir(id);
            ra.addFlashAttribute("sucesso", "Tamanho excluido.");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/tamanhos";
    }
}
