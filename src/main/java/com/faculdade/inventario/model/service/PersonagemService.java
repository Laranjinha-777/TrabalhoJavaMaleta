package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.Personagem;
import com.faculdade.inventario.model.repository.MaletaRepository;
import com.faculdade.inventario.model.repository.PersonagemRepository;
import com.faculdade.inventario.model.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonagemService {

    private final PersonagemRepository personagemRepository;
    private final MaletaRepository maletaRepository;
    private final TransacaoRepository transacaoRepository;

    public PersonagemService(PersonagemRepository personagemRepository, MaletaRepository maletaRepository,
                             TransacaoRepository transacaoRepository) {
        this.personagemRepository = personagemRepository;
        this.maletaRepository = maletaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Personagem> listarTodos() {
        return personagemRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Personagem buscarPorId(Long id) {
        return personagemRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Personagem nao encontrado: " + id));
    }

    @Transactional
    public Personagem salvar(Personagem personagem) {
        if (personagem.getVida() > personagem.getVidaMaxima()) {
            throw new RegraNegocioException("A vida nao pode ser maior que a vida maxima.");
        }
        return personagemRepository.save(personagem);
    }

    @Transactional
    public void excluir(Long id) {
        Personagem personagem = buscarPorId(id);
        long qtd = maletaRepository.countByPersonagemId(id);
        if (qtd > 0) {
            throw new RegraNegocioException("\"" + personagem.getNome() + "\" possui " + qtd
                    + " maleta(s). Exclua ou transfira as maletas antes.");
        }
        transacaoRepository.deleteByPersonagemId(id);
        personagemRepository.delete(personagem);
    }

    @Transactional(readOnly = true)
    public long contarMaletas(Long personagemId) {
        return maletaRepository.countByPersonagemId(personagemId);
    }

    @Transactional(readOnly = true)
    public long contar() {
        return personagemRepository.count();
    }
}
