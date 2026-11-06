package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.AtributoArma;
import com.faculdade.inventario.model.entity.Melhoria;
import com.faculdade.inventario.model.entity.TipoCategoria;
import com.faculdade.inventario.model.service.ItemService;
import com.faculdade.inventario.model.service.MelhoriaService;
import com.faculdade.inventario.model.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/melhorias")
public class MelhoriaController {

    private final MelhoriaService melhoriaService;
    private final ItemService itemService;

    public MelhoriaController(MelhoriaService melhoriaService, ItemService itemService) {
        this.melhoriaService = melhoriaService;
        this.itemService = itemService;
    }

    @ModelAttribute("atributos")
    public AtributoArma[] atributos() {
        return AtributoArma.values();
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Long armaId, Model model) {
        model.addAttribute("melhorias", melhoriaService.listar(armaId));
        model.addAttribute("armas", itemService.listarPorTipo(TipoCategoria.ARMA));
        model.addAttribute("armaId", armaId);
        return "melhorias/lista";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long armaId, Model model) {
        Melhoria m = new Melhoria();
        if (armaId != null) {
            m.setArma(itemService.buscarPorId(armaId));
        }
        return formulario(m, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(melhoriaService.buscarPorId(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("melhoria") Melhoria melhoria, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulario(melhoria, model);
        }
        try {
            melhoriaService.salvar(melhoria);
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return formulario(melhoria, model);
        }
        ra.addFlashAttribute("sucesso", "Melhoria salva.");
        return "redirect:/melhorias?armaId=" + melhoria.getArma().getId();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        Long armaId = melhoriaService.buscarPorId(id).getArma().getId();
        melhoriaService.excluir(id);
        ra.addFlashAttribute("sucesso", "Melhoria excluida.");
        return "redirect:/melhorias?armaId=" + armaId;
    }

    private String formulario(Melhoria m, Model model) {
        model.addAttribute("melhoria", m);
        model.addAttribute("armas", itemService.listarPorTipo(TipoCategoria.ARMA));
        return "melhorias/form";
    }
}
