package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.ItemMaleta;
import com.faculdade.inventario.model.entity.Maleta;
import com.faculdade.inventario.model.entity.TamanhoMaleta;
import com.faculdade.inventario.model.repository.ItemMaletaRepository;
import com.faculdade.inventario.model.repository.MaletaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaletaService {

    private final MaletaRepository maletaRepository;
    private final ItemMaletaRepository itemMaletaRepository;

    public MaletaService(MaletaRepository maletaRepository, ItemMaletaRepository itemMaletaRepository) {
        this.maletaRepository = maletaRepository;
        this.itemMaletaRepository = itemMaletaRepository;
    }

    @Transactional(readOnly = true)
    public List<Maleta> listarTodas() {
        return maletaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Maleta buscarPorId(Long id) {
        return maletaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Maleta nao encontrada: " + id));
    }

    @Transactional
    public Maleta salvar(Maleta maleta) {
        if (maleta.getId() != null) {
            validarTamanho(maleta.getId(), maleta.getTamanho());
        }
        return maletaRepository.save(maleta);
    }

    void validarTamanho(Long maletaId, TamanhoMaleta tamanho) {
        for (ItemMaleta im : itemMaletaRepository.findByMaletaIdOrderByPosicaoYAscPosicaoXAsc(maletaId)) {
            if (!im.isNaGrade() || im.getPosicaoX() == null) {
                continue;
            }
            boolean fora = im.getPosicaoX() + im.getLarguraOcupada() > tamanho.getColunas()
                    || im.getPosicaoY() + im.getAlturaOcupada() > tamanho.getLinhas();
            if (fora) {
                throw new RegraNegocioException("\"" + im.getItem().getNome()
                        + "\" ficaria fora da grade no tamanho " + tamanho.getNome() + ". Reorganize a maleta antes.");
            }
        }
    }

    @Transactional
    public void excluir(Long id) {
        maletaRepository.delete(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public long celulasOcupadas(Long maletaId) {
        return itemMaletaRepository.somaCelulasOcupadas(maletaId);
    }

    @Transactional(readOnly = true)
    public long contarItens(Long maletaId) {
        return itemMaletaRepository.countByMaletaId(maletaId);
    }

    @Transactional(readOnly = true)
    public long contar() {
        return maletaRepository.count();
    }
}
