package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.AtributoArma;
import com.faculdade.inventario.model.entity.ItemMaleta;
import com.faculdade.inventario.model.entity.Maleta;
import com.faculdade.inventario.model.entity.TipoCategoria;
import com.faculdade.inventario.model.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Controller
@RequestMapping("/mercador")
public class MercadorController {

    private final MercadorService mercadorService;
    private final MaletaService maletaService;
    private final ItemService itemService;
    private final ItemMaletaService itemMaletaService;
    private final MelhoriaService melhoriaService;
    private final TamanhoMaletaService tamanhoService;
    private final TransacaoService transacaoService;

    public MercadorController(MercadorService mercadorService, MaletaService maletaService, ItemService itemService,
                              ItemMaletaService itemMaletaService, MelhoriaService melhoriaService,
                              TamanhoMaletaService tamanhoService, TransacaoService transacaoService) {
        this.mercadorService = mercadorService;
        this.maletaService = maletaService;
        this.itemService = itemService;
        this.itemMaletaService = itemMaletaService;
        this.melhoriaService = melhoriaService;
        this.tamanhoService = tamanhoService;
        this.transacaoService = transacaoService;
    }

    @GetMapping
    public String inicio(RedirectAttributes ra) {
        List<Maleta> maletas = maletaService.listarTodas();
        if (maletas.isEmpty()) {
            ra.addFlashAttribute("erro", "Cadastre uma maleta antes de visitar o mercador.");
            return "redirect:/maletas";
        }
        return "redirect:/mercador/" + maletas.get(0).getId();
    }

    @GetMapping("/{maletaId}")
    public String loja(@PathVariable Long maletaId, @RequestParam(defaultValue = "comprar") String aba,
                       @RequestParam(required = false) Long arma, Model model) {
        Maleta maleta = maletaService.buscarPorId(maletaId);
        List<ItemMaleta> itens = itemMaletaService.listarPorMaleta(maletaId);

        model.addAttribute("maleta", maleta);
        model.addAttribute("maletas", maletaService.listarTodas());
        model.addAttribute("personagem", maleta.getPersonagem());
        model.addAttribute("aba", aba);
        model.addAttribute("grade", itemMaletaService.listarGrade(maletaId));
        model.addAttribute("transacoes", transacaoService.ultimas(maleta.getPersonagem().getId()));

        model.addAttribute("aVenda", itemService.listarAVenda());
        model.addAttribute("maletasMaiores", tamanhoService.maioresQue(maleta.getTamanho()));

        Map<Long, Integer> valores = new LinkedHashMap<>();
        itens.forEach(im -> valores.put(im.getId(), mercadorService.valorVenda(im)));
        model.addAttribute("itensVenda", itens.stream()
                .filter(im -> im.getItem().getCategoria().getTipo() != TipoCategoria.CHAVE).toList());
        model.addAttribute("valorVenda", valores);

        List<ItemMaleta> armas = itens.stream().filter(im -> im.getItem().isArma()).toList();
        model.addAttribute("armas", armas);
        ItemMaleta selecionada = armas.stream().filter(a -> a.getId().equals(arma)).findFirst()
                .orElse(armas.isEmpty() ? null : armas.get(0));
        model.addAttribute("armaSelecionada", selecionada);
        if (selecionada != null) {
            model.addAttribute("stats", melhoriaService.estatisticas(selecionada));
        }
        return "mercador/loja";
    }

    @PostMapping("/{maletaId}/comprar")
    public String comprar(@PathVariable Long maletaId, @RequestParam Long itemId, RedirectAttributes ra) {
        return acao(ra, "redirect:/mercador/" + maletaId + "?aba=comprar",
                () -> mercadorService.comprar(maletaId, itemId));
    }

    @PostMapping("/{maletaId}/maleta")
    public String comprarMaleta(@PathVariable Long maletaId, @RequestParam Long tamanhoId, RedirectAttributes ra) {
        return acao(ra, "redirect:/mercador/" + maletaId + "?aba=comprar",
                () -> mercadorService.comprarMaleta(maletaId, tamanhoId));
    }

    @PostMapping("/{maletaId}/vender")
    public String vender(@PathVariable Long maletaId, @RequestParam Long itemMaletaId, RedirectAttributes ra) {
        return acao(ra, "redirect:/mercador/" + maletaId + "?aba=vender",
                () -> mercadorService.vender(itemMaletaId));
    }

    @PostMapping("/{maletaId}/melhorar")
    public String melhorar(@PathVariable Long maletaId, @RequestParam Long armaId,
                           @RequestParam AtributoArma atributo, RedirectAttributes ra) {
        return acao(ra, "redirect:/mercador/" + maletaId + "?aba=melhorar&arma=" + armaId,
                () -> mercadorService.melhorar(armaId, atributo));
    }

    private String acao(RedirectAttributes ra, String destino, Supplier<String> acao) {
        try {
            ra.addFlashAttribute("sucesso", acao.get());
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return destino;
    }
}
