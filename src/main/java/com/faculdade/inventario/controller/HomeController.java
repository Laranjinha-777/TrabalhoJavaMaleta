package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CategoriaService categoriaService;
    private final ItemService itemService;
    private final PersonagemService personagemService;
    private final MaletaService maletaService;
    private final ItemMaletaService itemMaletaService;
    private final TransacaoService transacaoService;

    public HomeController(CategoriaService categoriaService, ItemService itemService,
                          PersonagemService personagemService, MaletaService maletaService,
                          ItemMaletaService itemMaletaService, TransacaoService transacaoService) {
        this.categoriaService = categoriaService;
        this.itemService = itemService;
        this.personagemService = personagemService;
        this.maletaService = maletaService;
        this.itemMaletaService = itemMaletaService;
        this.transacaoService = transacaoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalCategorias", categoriaService.contar());
        model.addAttribute("totalItens", itemService.contar());
        model.addAttribute("totalPersonagens", personagemService.contar());
        model.addAttribute("totalMaletas", maletaService.contar());
        model.addAttribute("totalGuardados", itemMaletaService.contar());
        model.addAttribute("maletas", maletaService.listarTodas());
        model.addAttribute("transacoes", transacaoService.listarTodas().stream().limit(6).toList());
        return "index";
    }
}
