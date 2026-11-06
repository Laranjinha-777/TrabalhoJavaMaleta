package com.faculdade.inventario.model.repository;

import com.faculdade.inventario.model.entity.ItemMaleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemMaletaRepository extends JpaRepository<ItemMaleta, Long> {

    List<ItemMaleta> findByMaletaIdOrderByPosicaoYAscPosicaoXAsc(Long maletaId);

    List<ItemMaleta> findByMaletaIdAndItemIdOrderByQuantidadeAsc(Long maletaId, Long itemId);

    List<ItemMaleta> findAllByOrderByMaletaNomeAscPosicaoYAscPosicaoXAsc();

    long countByItemId(Long itemId);

    long countByMaletaId(Long maletaId);

    @Query("select coalesce(sum(im.item.largura * im.item.altura), 0) "
            + "from ItemMaleta im where im.maleta.id = :maletaId and im.posicaoX is not null")
    long somaCelulasOcupadas(@Param("maletaId") Long maletaId);
}
