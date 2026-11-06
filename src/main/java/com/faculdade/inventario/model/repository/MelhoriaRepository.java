package com.faculdade.inventario.model.repository;

import com.faculdade.inventario.model.entity.AtributoArma;
import com.faculdade.inventario.model.entity.Melhoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MelhoriaRepository extends JpaRepository<Melhoria, Long> {

    List<Melhoria> findByArmaIdOrderByAtributoAscNivelAsc(Long armaId);

    List<Melhoria> findAllByOrderByArmaNomeAscAtributoAscNivelAsc();

    List<Melhoria> findByArmaIdAndAtributoOrderByNivelAsc(Long armaId, AtributoArma atributo);

    Optional<Melhoria> findByArmaIdAndAtributoAndNivel(Long armaId, AtributoArma atributo, Integer nivel);

    long countByArmaId(Long armaId);

    void deleteByArmaId(Long armaId);
}
