package com.faculdade.inventario.model.repository;

import com.faculdade.inventario.model.entity.Maleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaletaRepository extends JpaRepository<Maleta, Long> {

    List<Maleta> findAllByOrderByNomeAsc();

    long countByPersonagemId(Long personagemId);

    long countByTamanhoId(Long tamanhoId);
}
