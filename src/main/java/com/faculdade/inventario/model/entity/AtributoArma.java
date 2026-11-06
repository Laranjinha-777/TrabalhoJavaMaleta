package com.faculdade.inventario.model.entity;

public enum AtributoArma {
    PODER("Poder de fogo", "%.1f"),
    CADENCIA("Velocidade de disparo", "%.2f"),
    RECARGA("Velocidade de recarga", "%.2f"),
    CAPACIDADE("Capacidade", "%.0f"),
    EXCLUSIVA("Melhoria exclusiva", "");

    private final String rotulo;
    private final String formato;

    AtributoArma(String rotulo, String formato) {
        this.rotulo = rotulo;
        this.formato = formato;
    }

    public String getRotulo() { return rotulo; }

    public String formatar(Double valor) {
        if (valor == null || formato.isEmpty()) {
            return "—";
        }
        return String.format(java.util.Locale.US, formato, valor);
    }
}
