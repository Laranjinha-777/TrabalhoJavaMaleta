package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.TamanhoMaleta;
import com.faculdade.inventario.model.repository.MaletaRepository;
import com.faculdade.inventario.model.repository.TamanhoMaletaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TamanhoMaletaService {

    private final TamanhoMaletaRepository tamanhoRepository;
    private final MaletaRepository maletaRepository;

    public TamanhoMaletaService(TamanhoMaletaRepository tamanhoRepository, MaletaRepository maletaRepository) {
        this.tamanhoRepository = tamanhoRepository;
        this.maletaRepository = maletaRepository;
    }

    @Transactional(readOnly = true)
    public List<TamanhoMaleta> listarTodos() {
        return tamanhoRepository.findAllByOrderByOrdemAsc();
    }

    @Transactional(readOnly = true)
    public List<TamanhoMaleta> maioresQue(TamanhoMaleta atual) {
        return tamanhoRepository.findByOrdemGreaterThanOrderByOrdemAsc(atual.getOrdem());
    }

    @Transactional(readOnly = true)
    public TamanhoMaleta buscarPorId(Long id) {
        return tamanhoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Tamanho de maleta nao encontrado: " + id));
    }

    @Transactional
    public TamanhoMaleta salvar(TamanhoMaleta tamanho) {
        return tamanhoRepository.save(tamanho);
    }

    @Transactional
    public void excluir(Long id) {
        TamanhoMaleta t = buscarPorId(id);
        long usos = maletaRepository.countByTamanhoId(id);
        if (usos > 0) {
            throw new RegraNegocioException("O tamanho \"" + t.getNome() + "\" esta em uso por " + usos + " maleta(s).");
        }
        tamanhoRepository.delete(t);
    }

    @Transactional(readOnly = true)
    public long contar() {
        return tamanhoRepository.count();
    }
}
