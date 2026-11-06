package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.AtributoArma;

public record EstatisticaArma(AtributoArma atributo, int nivel, int nivelMaximo, String valorAtual,
                              String proximoValor, Integer proximoCusto, String descricao, boolean disponivel) {

    public boolean isNoMaximo() {
        return nivel >= nivelMaximo;
    }

    public String getRotulo() {
        return atributo.getRotulo();
    }
}
