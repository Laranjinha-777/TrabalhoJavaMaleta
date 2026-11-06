package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.Combinacao;
import com.faculdade.inventario.model.service.CombinacaoService;
import com.faculdade.inventario.model.service.ItemService;
import com.faculdade.inventario.model.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/combinacoes")
public class CombinacaoController {

    private final CombinacaoService combinacaoService;
    private final ItemService itemService;

    public CombinacaoController(CombinacaoService combinacaoService, ItemService itemService) {
        this.combinacaoService = combinacaoService;
        this.itemService = itemService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("combinacoes", combinacaoService.listarTodas());
        return "combinacoes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new Combinacao(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(combinacaoService.buscarPorId(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("combinacao") Combinacao combinacao, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulario(combinacao, model);
        }
        try {
            combinacaoService.salvar(combinacao);
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return formulario(combinacao, model);
        }
        ra.addFlashAttribute("sucesso", "Combinacao salva.");
        return "redirect:/combinacoes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        combinacaoService.excluir(id);
        ra.addFlashAttribute("sucesso", "Combinacao excluida.");
        return "redirect:/combinacoes";
    }

    private String formulario(Combinacao c, Model model) {
        model.addAttribute("combinacao", c);
        model.addAttribute("itens", itemService.listarTodos());
        return "combinacoes/form";
    }
}
