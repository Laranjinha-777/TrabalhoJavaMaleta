package com.faculdade.inventario.model.repository;

import com.faculdade.inventario.model.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findAllByOrderByDataHoraDesc();

    List<Transacao> findTop8ByPersonagemIdOrderByDataHoraDesc(Long personagemId);

    void deleteByPersonagemId(Long personagemId);
}
