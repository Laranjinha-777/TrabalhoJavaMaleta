package com.faculdade.inventario.model.repository;

import com.faculdade.inventario.model.entity.Combinacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CombinacaoRepository extends JpaRepository<Combinacao, Long> {

    List<Combinacao> findAllByOrderByResultadoNomeAsc();

    @Query("select c from Combinacao c where (c.itemA.id = :a and c.itemB.id = :b) or (c.itemA.id = :b and c.itemB.id = :a)")
    List<Combinacao> buscarReceita(@Param("a") Long itemA, @Param("b") Long itemB);

    @Query("select count(c) from Combinacao c where c.itemA.id = :id or c.itemB.id = :id or c.resultado.id = :id")
    long contarUsoDoItem(@Param("id") Long itemId);

    default Optional<Combinacao> receita(Long a, Long b) {
        return buscarReceita(a, b).stream().findFirst();
    }
}
