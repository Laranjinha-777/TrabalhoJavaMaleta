package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.Categoria;
import com.faculdade.inventario.model.entity.TipoCategoria;
import com.faculdade.inventario.model.service.CategoriaService;
import com.faculdade.inventario.model.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @ModelAttribute("tipos")
    public TipoCategoria[] tipos() {
        return TipoCategoria.values();
    }

    @GetMapping
    public String listar(Model model) {
        var categorias = categoriaService.listarTodas();
        Map<Long, Long> qtdItens = new LinkedHashMap<>();
        categorias.forEach(c -> qtdItens.put(c.getId(), categoriaService.contarItens(c.getId())));
        model.addAttribute("categorias", categorias);
        model.addAttribute("qtdItens", qtdItens);
        return "categorias/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.buscarPorId(id));
        return "categorias/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("categoria") Categoria categoria, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "categorias/form";
        }
        try {
            categoriaService.salvar(categoria);
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return "categorias/form";
        }
        ra.addFlashAttribute("sucesso", "Categoria \"" + categoria.getNome() + "\" salva com sucesso.");
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoriaService.excluir(id);
            ra.addFlashAttribute("sucesso", "Categoria excluida.");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/categorias";
    }
}
