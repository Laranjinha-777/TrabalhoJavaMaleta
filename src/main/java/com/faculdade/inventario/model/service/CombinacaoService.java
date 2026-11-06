package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.Combinacao;
import com.faculdade.inventario.model.repository.CombinacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CombinacaoService {

    private final CombinacaoRepository combinacaoRepository;

    public CombinacaoService(CombinacaoRepository combinacaoRepository) {
        this.combinacaoRepository = combinacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Combinacao> listarTodas() {
        return combinacaoRepository.findAllByOrderByResultadoNomeAsc();
    }

    @Transactional(readOnly = true)
    public Combinacao buscarPorId(Long id) {
        return combinacaoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Combinacao nao encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Combinacao> receita(Long itemA, Long itemB) {
        return combinacaoRepository.receita(itemA, itemB);
    }

    @Transactional
    public Combinacao salvar(Combinacao c) {
        receita(c.getItemA().getId(), c.getItemB().getId()).ifPresent(outra -> {
            if (!outra.getId().equals(c.getId())) {
                throw new RegraNegocioException("Ja existe uma combinacao para " + c.getItemA().getNome()
                        + " + " + c.getItemB().getNome() + ".");
            }
        });
        return combinacaoRepository.save(c);
    }

    @Transactional
    public void excluir(Long id) {
        combinacaoRepository.delete(buscarPorId(id));
    }
}
