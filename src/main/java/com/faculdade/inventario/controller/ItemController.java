package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.Item;
import com.faculdade.inventario.model.entity.TipoCategoria;
import com.faculdade.inventario.model.service.CategoriaService;
import com.faculdade.inventario.model.service.ImagemService;
import com.faculdade.inventario.model.service.ItemService;
import com.faculdade.inventario.model.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/itens")
public class ItemController {

    private final ItemService itemService;
    private final CategoriaService categoriaService;
    private final ImagemService imagemService;

    public ItemController(ItemService itemService, CategoriaService categoriaService, ImagemService imagemService) {
        this.itemService = itemService;
        this.categoriaService = categoriaService;
        this.imagemService = imagemService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                         @RequestParam(required = false) Long categoriaId, Model model) {
        model.addAttribute("itens", itemService.pesquisar(nome, categoriaId));
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("nome", nome);
        model.addAttribute("categoriaId", categoriaId);
        return "itens/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new Item(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(itemService.buscarPorId(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("item") Item item, BindingResult result,
                         @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulario(item, model);
        }
        try {
            if (arquivo != null && !arquivo.isEmpty()) {
                item.setImagem(imagemService.salvar(arquivo));
            }
            itemService.salvar(item);
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return formulario(item, model);
        }
        ra.addFlashAttribute("sucesso", "Item \"" + item.getNome() + "\" salvo com sucesso.");
        return "redirect:/itens";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            itemService.excluir(id);
            ra.addFlashAttribute("sucesso", "Item excluido.");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/itens";
    }

    private String formulario(Item item, Model model) {
        model.addAttribute("item", item);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("municoes", itemService.listarPorTipo(TipoCategoria.MUNICAO));
        model.addAttribute("icones", imagemService.listarIcones());
        return "itens/form";
    }
}
