package com.faculdade.inventario.model.repository;

import com.faculdade.inventario.model.entity.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonagemRepository extends JpaRepository<Personagem, Long> {

    List<Personagem> findAllByOrderByNomeAsc();
}
