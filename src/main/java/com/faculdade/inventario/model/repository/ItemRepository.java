package com.faculdade.inventario.model.repository;

import com.faculdade.inventario.model.entity.Item;
import com.faculdade.inventario.model.entity.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOrderByNomeAsc();

    List<Item> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Item> findByCategoriaTipoOrderByNomeAsc(TipoCategoria tipo);

    List<Item> findByVendidoMercadorTrueOrderByCategoriaNomeAscPrecoAsc();

    long countByCategoriaId(Long categoriaId);

    long countByMunicaoId(Long municaoId);
}
