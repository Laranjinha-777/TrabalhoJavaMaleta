package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.Personagem;
import com.faculdade.inventario.model.entity.TipoTransacao;
import com.faculdade.inventario.model.entity.Transacao;
import com.faculdade.inventario.model.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Transacao> listarTodas() {
        return transacaoRepository.findAllByOrderByDataHoraDesc();
    }

    @Transactional(readOnly = true)
    public List<Transacao> ultimas(Long personagemId) {
        return transacaoRepository.findTop8ByPersonagemIdOrderByDataHoraDesc(personagemId);
    }

    @Transactional
    public void registrar(Personagem p, TipoTransacao tipo, String descricao, int valor) {
        transacaoRepository.save(new Transacao(p, tipo, descricao, valor));
    }

    @Transactional
    public void excluir(Long id) {
        if (!transacaoRepository.existsById(id)) {
            throw new RegraNegocioException("Transacao nao encontrada: " + id);
        }
        transacaoRepository.deleteById(id);
    }
}
