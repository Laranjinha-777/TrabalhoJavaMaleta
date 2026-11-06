package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.ItemMaleta;
import com.faculdade.inventario.model.entity.Maleta;
import com.faculdade.inventario.model.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/maletas")
public class MaletaController {

    private final MaletaService maletaService;
    private final PersonagemService personagemService;
    private final ItemMaletaService itemMaletaService;
    private final ItemService itemService;
    private final TamanhoMaletaService tamanhoService;
    private final MelhoriaService melhoriaService;
    private final MercadorService mercadorService;

    public MaletaController(MaletaService maletaService, PersonagemService personagemService,
                            ItemMaletaService itemMaletaService, ItemService itemService,
                            TamanhoMaletaService tamanhoService, MelhoriaService melhoriaService,
                            MercadorService mercadorService) {
        this.maletaService = maletaService;
        this.personagemService = personagemService;
        this.itemMaletaService = itemMaletaService;
        this.itemService = itemService;
        this.tamanhoService = tamanhoService;
        this.melhoriaService = melhoriaService;
        this.mercadorService = mercadorService;
    }

    @GetMapping
    public String listar(Model model) {
        var maletas = maletaService.listarTodas();
        Map<Long, Long> ocupadas = new LinkedHashMap<>();
        Map<Long, Long> qtdItens = new LinkedHashMap<>();
        maletas.forEach(m -> {
            ocupadas.put(m.getId(), maletaService.celulasOcupadas(m.getId()));
            qtdItens.put(m.getId(), maletaService.contarItens(m.getId()));
        });
        model.addAttribute("maletas", maletas);
        model.addAttribute("ocupadas", ocupadas);
        model.addAttribute("qtdItens", qtdItens);
        return "maletas/lista";
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model) {
        Maleta maleta = maletaService.buscarPorId(id);
        List<ItemMaleta> grade = itemMaletaService.listarGrade(id);
        model.addAttribute("maleta", maleta);
        model.addAttribute("personagem", maleta.getPersonagem());
        model.addAttribute("grade", grade);
        model.addAttribute("foraDaGrade", itemMaletaService.listarForaDaGrade(id));
        model.addAttribute("ocupadas", maletaService.celulasOcupadas(id));
        model.addAttribute("itens", itemService.listarTodos());
        adicionarInfoArmas(id, grade, model);
        return "maletas/visualizar";
    }

    @PostMapping("/{id}/guardar")
    public String guardar(@PathVariable Long id, @RequestParam Long itemId,
                          @RequestParam(defaultValue = "1") int quantidade, RedirectAttributes ra) {
        try {
            var im = itemMaletaService.guardarAutomaticamente(id, itemId, quantidade);
            ra.addFlashAttribute("sucesso", "Pegou " + im.getItem().getNome() + ".");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/maletas/" + id;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new Maleta(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(maletaService.buscarPorId(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("maleta") Maleta maleta, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulario(maleta, model);
        }
        try {
            maletaService.salvar(maleta);
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return formulario(maleta, model);
        }
        ra.addFlashAttribute("sucesso", "Maleta \"" + maleta.getNome() + "\" salva com sucesso.");
        return "redirect:/maletas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            maletaService.excluir(id);
            ra.addFlashAttribute("sucesso", "Maleta excluida (junto com os itens dentro dela).");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/maletas";
    }

    private String formulario(Maleta maleta, Model model) {
        model.addAttribute("maleta", maleta);
        model.addAttribute("personagens", personagemService.listarTodos());
        model.addAttribute("tamanhos", tamanhoService.listarTodos());
        return "maletas/form";
    }

    private void adicionarInfoArmas(Long maletaId, List<ItemMaleta> itens, Model model) {
        Map<Long, List<EstatisticaArma>> stats = new HashMap<>();
        Map<Long, Integer> capacidade = new HashMap<>();
        Map<Long, Integer> municao = new HashMap<>();
        Map<Long, Integer> valorVenda = new HashMap<>();
        for (ItemMaleta im : itens) {
            valorVenda.put(im.getId(), mercadorService.valorVenda(im));
            if (im.getItem().isArma()) {
                stats.put(im.getId(), melhoriaService.estatisticas(im));
                capacidade.put(im.getId(), melhoriaService.capacidade(im));
                municao.put(im.getId(), itemMaletaService.municaoDisponivel(maletaId, im.getItem().getMunicao()));
            }
        }
        model.addAttribute("stats", stats);
        model.addAttribute("capacidade", capacidade);
        model.addAttribute("municaoDisponivel", municao);
        model.addAttribute("valorVenda", valorVenda);
    }
}
