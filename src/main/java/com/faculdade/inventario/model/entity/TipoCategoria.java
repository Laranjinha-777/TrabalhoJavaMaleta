package com.faculdade.inventario.model.entity;

public enum TipoCategoria {
    ARMA("Arma", true),
    MUNICAO("Munição", true),
    CURA("Recuperação", true),
    GRANADA("Granada", true),
    TESOURO("Tesouro", false),
    CHAVE("Item-chave", false);

    private final String rotulo;
    private final boolean ocupaMaleta;

    TipoCategoria(String rotulo, boolean ocupaMaleta) {
        this.rotulo = rotulo;
        this.ocupaMaleta = ocupaMaleta;
    }

    public String getRotulo() { return rotulo; }
    public boolean isOcupaMaleta() { return ocupaMaleta; }
}
