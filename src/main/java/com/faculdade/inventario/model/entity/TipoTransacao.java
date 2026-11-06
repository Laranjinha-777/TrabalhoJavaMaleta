package com.faculdade.inventario.model.entity;

public enum TipoTransacao {
    COMPRA("Compra"),
    VENDA("Venda"),
    MELHORIA("Melhoria"),
    MALETA("Maleta");

    private final String rotulo;

    TipoTransacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() { return rotulo; }
}
