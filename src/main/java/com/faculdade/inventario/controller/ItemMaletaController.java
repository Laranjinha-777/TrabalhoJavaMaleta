package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.ItemMaleta;
import com.faculdade.inventario.model.service.ItemMaletaService;
import com.faculdade.inventario.model.service.ItemService;
import com.faculdade.inventario.model.service.MaletaService;
import com.faculdade.inventario.model.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Supplier;

@Controller
@RequestMapping("/guardados")
public class ItemMaletaController {

    private final ItemMaletaService itemMaletaService;
    private final MaletaService maletaService;
    private final ItemService itemService;

    public ItemMaletaController(ItemMaletaService itemMaletaService, MaletaService maletaService,
                                ItemService itemService) {
        this.itemMaletaService = itemMaletaService;
        this.maletaService = maletaService;
        this.itemService = itemService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("guardados", itemMaletaService.listarTodos());
        return "guardados/lista";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long maletaId,
                       @RequestParam(required = false) Integer x,
                       @RequestParam(required = false) Integer y, Model model) {
        ItemMaleta im = new ItemMaleta();
        if (maletaId != null) {
            im.setMaleta(maletaService.buscarPorId(maletaId));
        }
        if (x != null) {
            im.setPosicaoX(x);
        }
        if (y != null) {
            im.setPosicaoY(y);
        }
        return formulario(im, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(itemMaletaService.buscarPorId(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("itemMaleta") ItemMaleta itemMaleta, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulario(itemMaleta, model);
        }
        try {
            itemMaletaService.salvar(itemMaleta);
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return formulario(itemMaleta, model);
        }
        ra.addFlashAttribute("sucesso", "\"" + itemMaleta.getItem().getNome() + "\" salvo na maleta.");
        return "redirect:/maletas/" + itemMaleta.getMaleta().getId();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          @RequestParam(defaultValue = "maleta") String voltarPara, RedirectAttributes ra) {
        ItemMaleta im = itemMaletaService.buscarPorId(id);
        itemMaletaService.excluir(id);
        ra.addFlashAttribute("sucesso", im.getItem().getNome() + " descartado.");
        return "lista".equals(voltarPara) ? "redirect:/guardados" : "redirect:/maletas/" + im.getMaleta().getId();
    }

    @PostMapping("/{id}/mover")
    public String mover(@PathVariable Long id, @RequestParam int x, @RequestParam int y,
                        @RequestParam(defaultValue = "false") boolean rotacionado, RedirectAttributes ra) {
        return acao(id, ra, () -> {
            itemMaletaService.mover(id, x, y, rotacionado);
            return null;
        });
    }

    @PostMapping("/{id}/rotacionar")
    public String rotacionar(@PathVariable Long id, RedirectAttributes ra) {
        return acao(id, ra, () -> {
            itemMaletaService.rotacionar(id);
            return null;
        });
    }

    @PostMapping("/{id}/usar")
    public String usar(@PathVariable Long id, RedirectAttributes ra) {
        return acao(id, ra, () -> itemMaletaService.usar(id));
    }

    @PostMapping("/{id}/equipar")
    public String equipar(@PathVariable Long id, RedirectAttributes ra) {
        return acao(id, ra, () -> itemMaletaService.equipar(id));
    }

    @PostMapping("/{id}/recarregar")
    public String recarregar(@PathVariable Long id, RedirectAttributes ra) {
        return acao(id, ra, () -> itemMaletaService.recarregar(id));
    }

    @PostMapping("/{id}/combinar")
    public String combinar(@PathVariable Long id, @RequestParam Long alvoId, RedirectAttributes ra) {
        return acao(id, ra, () -> itemMaletaService.combinar(id, alvoId));
    }

    private String acao(Long id, RedirectAttributes ra, Supplier<String> acao) {
        Long maletaId = itemMaletaService.buscarPorId(id).getMaleta().getId();
        try {
            String msg = acao.get();
            if (msg != null) {
                ra.addFlashAttribute("sucesso", msg);
            }
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/maletas/" + maletaId;
    }

    private String formulario(ItemMaleta im, Model model) {
        model.addAttribute("itemMaleta", im);
        model.addAttribute("maletas", maletaService.listarTodas());
        model.addAttribute("itens", itemService.listarTodos());
        return "guardados/form";
    }
}
