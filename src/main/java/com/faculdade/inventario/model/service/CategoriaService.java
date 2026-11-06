package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.Categoria;
import com.faculdade.inventario.model.repository.CategoriaRepository;
import com.faculdade.inventario.model.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ItemRepository itemRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ItemRepository itemRepository) {
        this.categoriaRepository = categoriaRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Categoria nao encontrada: " + id));
    }

    @Transactional
    public Categoria salvar(Categoria categoria) {
        boolean duplicada = categoria.getId() == null
                ? categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())
                : categoriaRepository.existsByNomeIgnoreCaseAndIdNot(categoria.getNome(), categoria.getId());
        if (duplicada) {
            throw new RegraNegocioException("Ja existe uma categoria chamada \"" + categoria.getNome() + "\".");
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);
        long qtdItens = itemRepository.countByCategoriaId(id);
        if (qtdItens > 0) {
            throw new RegraNegocioException("A categoria \"" + categoria.getNome() + "\" possui "
                    + qtdItens + " item(ns) e nao pode ser excluida.");
        }
        categoriaRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public long contarItens(Long categoriaId) {
        return itemRepository.countByCategoriaId(categoriaId);
    }

    @Transactional(readOnly = true)
    public long contar() {
        return categoriaRepository.count();
    }
}
