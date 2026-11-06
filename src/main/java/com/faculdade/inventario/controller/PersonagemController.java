package com.faculdade.inventario.controller;

import com.faculdade.inventario.model.entity.Personagem;
import com.faculdade.inventario.model.service.PersonagemService;
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
@RequestMapping("/personagens")
public class PersonagemController {

    private final PersonagemService personagemService;

    public PersonagemController(PersonagemService personagemService) {
        this.personagemService = personagemService;
    }

    @GetMapping
    public String listar(Model model) {
        var personagens = personagemService.listarTodos();
        Map<Long, Long> qtdMaletas = new LinkedHashMap<>();
        personagens.forEach(p -> qtdMaletas.put(p.getId(), personagemService.contarMaletas(p.getId())));
        model.addAttribute("personagens", personagens);
        model.addAttribute("qtdMaletas", qtdMaletas);
        return "personagens/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("personagem", new Personagem());
        return "personagens/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("personagem", personagemService.buscarPorId(id));
        return "personagens/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("personagem") Personagem personagem, BindingResult result,
                         org.springframework.ui.Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "personagens/form";
        }
        try {
            personagemService.salvar(personagem);
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return "personagens/form";
        }
        ra.addFlashAttribute("sucesso", "Personagem \"" + personagem.getNome() + "\" salvo com sucesso.");
        return "redirect:/personagens";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            personagemService.excluir(id);
            ra.addFlashAttribute("sucesso", "Personagem excluido.");
        } catch (RegraNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/personagens";
    }
}
