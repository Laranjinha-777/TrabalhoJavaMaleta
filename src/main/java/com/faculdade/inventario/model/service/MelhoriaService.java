package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.AtributoArma;
import com.faculdade.inventario.model.entity.ItemMaleta;
import com.faculdade.inventario.model.entity.Melhoria;
import com.faculdade.inventario.model.repository.MelhoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MelhoriaService {

    private static final AtributoArma[] BASICOS = {
            AtributoArma.PODER, AtributoArma.CADENCIA, AtributoArma.RECARGA, AtributoArma.CAPACIDADE};

    private final MelhoriaRepository melhoriaRepository;

    public MelhoriaService(MelhoriaRepository melhoriaRepository) {
        this.melhoriaRepository = melhoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Melhoria> listar(Long armaId) {
        return armaId == null
                ? melhoriaRepository.findAllByOrderByArmaNomeAscAtributoAscNivelAsc()
                : melhoriaRepository.findByArmaIdOrderByAtributoAscNivelAsc(armaId);
    }

    @Transactional(readOnly = true)
    public Melhoria buscarPorId(Long id) {
        return melhoriaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Melhoria nao encontrada: " + id));
    }

    @Transactional
    public Melhoria salvar(Melhoria m) {
        if (!m.getArma().isArma()) {
            throw new RegraNegocioException("Melhorias so podem ser cadastradas para armas.");
        }
        Optional<Melhoria> existente = melhoriaRepository.findByArmaIdAndAtributoAndNivel(
                m.getArma().getId(), m.getAtributo(), m.getNivel());
        if (existente.isPresent() && !existente.get().getId().equals(m.getId())) {
            throw new RegraNegocioException("Ja existe o nivel " + m.getNivel() + " de "
                    + m.getAtributo().getRotulo() + " para " + m.getArma().getNome() + ".");
        }
        if (m.getAtributo() != AtributoArma.EXCLUSIVA && m.getValor() == null) {
            throw new RegraNegocioException("Informe o valor do atributo neste nivel.");
        }
        if (m.getNivel() == 1) {
            m.setCusto(0);
        }
        return melhoriaRepository.save(m);
    }

    @Transactional
    public void excluir(Long id) {
        melhoriaRepository.delete(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Optional<Melhoria> proximoNivel(ItemMaleta arma, AtributoArma atributo) {
        return melhoriaRepository.findByArmaIdAndAtributoAndNivel(
                arma.getItem().getId(), atributo, arma.getNivel(atributo) + 1);
    }

    @Transactional(readOnly = true)
    public List<EstatisticaArma> estatisticas(ItemMaleta arma) {
        List<EstatisticaArma> linhas = new ArrayList<>();
        if (!arma.getItem().isArma()) {
            return linhas;
        }
        Long armaId = arma.getItem().getId();
        Melhoria exclusiva = exclusivaAtiva(arma);
        boolean tudoNoMaximo = true;

        for (AtributoArma atr : BASICOS) {
            List<Melhoria> niveis = melhoriaRepository.findByArmaIdAndAtributoOrderByNivelAsc(armaId, atr);
            if (niveis.isEmpty()) {
                continue;
            }
            int nivel = arma.getNivel(atr);
            int max = niveis.get(niveis.size() - 1).getNivel();
            Melhoria atual = niveis.stream().filter(n -> n.getNivel() == nivel).findFirst().orElse(niveis.get(0));
            Melhoria prox = niveis.stream().filter(n -> n.getNivel() == nivel + 1).findFirst().orElse(null);

            Double valor = atual.getValor();
            if (exclusiva != null && exclusiva.getAtributoAfetado() == atr && exclusiva.getValor() != null) {
                valor = exclusiva.getValor();
            }
            if (nivel < max) {
                tudoNoMaximo = false;
            }
            linhas.add(new EstatisticaArma(atr, nivel, max, atr.formatar(valor),
                    prox == null ? null : atr.formatar(prox.getValor()),
                    prox == null ? null : prox.getCusto(), null, true));
        }

        melhoriaRepository.findByArmaIdAndAtributoAndNivel(armaId, AtributoArma.EXCLUSIVA, 2).ifPresent(ex -> {
            boolean comprada = arma.isExclusiva();
            linhas.add(new EstatisticaArma(AtributoArma.EXCLUSIVA, comprada ? 2 : 1, 2,
                    comprada ? "Ativa" : "—", null, comprada ? null : ex.getCusto(), ex.getDescricao(), false));
        });
        if (tudoNoMaximo) {
            for (int i = 0; i < linhas.size(); i++) {
                EstatisticaArma e = linhas.get(i);
                if (e.atributo() == AtributoArma.EXCLUSIVA) {
                    linhas.set(i, new EstatisticaArma(e.atributo(), e.nivel(), e.nivelMaximo(), e.valorAtual(),
                            e.proximoValor(), e.proximoCusto(), e.descricao(), true));
                }
            }
        }
        return linhas;
    }

    @Transactional(readOnly = true)
    public int capacidade(ItemMaleta arma) {
        Melhoria exclusiva = exclusivaAtiva(arma);
        if (exclusiva != null && exclusiva.getAtributoAfetado() == AtributoArma.CAPACIDADE && exclusiva.getValor() != null) {
            return exclusiva.getValor().intValue();
        }
        return melhoriaRepository.findByArmaIdAndAtributoAndNivel(arma.getItem().getId(), AtributoArma.CAPACIDADE,
                        arma.getNivelCapacidade())
                .map(m -> m.getValor() == null ? 0 : m.getValor().intValue())
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public boolean tudoNoMaximo(ItemMaleta arma) {
        return estatisticas(arma).stream()
                .filter(e -> e.atributo() != AtributoArma.EXCLUSIVA)
                .allMatch(EstatisticaArma::isNoMaximo);
    }

    private Melhoria exclusivaAtiva(ItemMaleta arma) {
        if (!arma.isExclusiva()) {
            return null;
        }
        return melhoriaRepository.findByArmaIdAndAtributoAndNivel(arma.getItem().getId(), AtributoArma.EXCLUSIVA, 2)
                .orElse(null);
    }
}
