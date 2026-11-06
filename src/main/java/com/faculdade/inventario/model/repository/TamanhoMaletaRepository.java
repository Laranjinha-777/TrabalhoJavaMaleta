package com.faculdade.inventario.model.repository;

import com.faculdade.inventario.model.entity.TamanhoMaleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TamanhoMaletaRepository extends JpaRepository<TamanhoMaleta, Long> {

    List<TamanhoMaleta> findAllByOrderByOrdemAsc();

    List<TamanhoMaleta> findByOrdemGreaterThanOrderByOrdemAsc(Integer ordem);
}
