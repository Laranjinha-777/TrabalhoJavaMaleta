package com.faculdade.inventario.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "item_maleta")
public class ItemMaleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Selecione a maleta")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "maleta_id", nullable = false)
    private Maleta maleta;

    @NotNull(message = "Selecione o item")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Min(value = 0, message = "Coluna minima 0")
    @Column(name = "posicao_x")
    private Integer posicaoX = 0;

    @Min(value = 0, message = "Linha minima 0")
    @Column(name = "posicao_y")
    private Integer posicaoY = 0;

    @NotNull(message = "Informe a quantidade")
    @Min(value = 1, message = "Quantidade minima 1")
    @Column(nullable = false)
    private Integer quantidade = 1;

    @Column(nullable = false)
    private boolean rotacionado = false;

    @Column(nullable = false)
    private boolean equipada = false;

    @Min(0)
    @Column(name = "municao_carregada", nullable = false)
    private Integer municaoCarregada = 0;

    @Min(1)
    @Column(name = "nivel_poder", nullable = false)
    private Integer nivelPoder = 1;

    @Min(1)
    @Column(name = "nivel_cadencia", nullable = false)
    private Integer nivelCadencia = 1;

    @Min(1)
    @Column(name = "nivel_recarga", nullable = false)
    private Integer nivelRecarga = 1;

    @Min(1)
    @Column(name = "nivel_capacidade", nullable = false)
    private Integer nivelCapacidade = 1;

    @Column(nullable = false)
    private boolean exclusiva = false;

    @Min(0)
    @Column(nullable = false)
    private Integer investido = 0;

    public ItemMaleta() {
    }

    public ItemMaleta(Maleta maleta, Item item, Integer posicaoX, Integer posicaoY, int quantidade, boolean rotacionado) {
        this.maleta = maleta;
        this.item = item;
        this.posicaoX = posicaoX;
        this.posicaoY = posicaoY;
        this.quantidade = quantidade;
        this.rotacionado = rotacionado;
    }

    public boolean isNaGrade() {
        return item != null && item.isOcupaMaleta();
    }

    public int getLarguraOcupada() {
        return rotacionado ? item.getAltura() : item.getLargura();
    }

    public int getAlturaOcupada() {
        return rotacionado ? item.getLargura() : item.getAltura();
    }

    public boolean sobrepoe(int x, int y, int w, int h) {
        if (!isNaGrade() || posicaoX == null || posicaoY == null) {
            return false;
        }
        return posicaoX < x + w && x < posicaoX + getLarguraOcupada()
                && posicaoY < y + h && y < posicaoY + getAlturaOcupada();
    }

    public int getNivel(AtributoArma atributo) {
        return switch (atributo) {
            case PODER -> nivelPoder;
            case CADENCIA -> nivelCadencia;
            case RECARGA -> nivelRecarga;
            case CAPACIDADE -> nivelCapacidade;
            case EXCLUSIVA -> exclusiva ? 2 : 1;
        };
    }

    public void setNivel(AtributoArma atributo, int nivel) {
        switch (atributo) {
            case PODER -> nivelPoder = nivel;
            case CADENCIA -> nivelCadencia = nivel;
            case RECARGA -> nivelRecarga = nivel;
            case CAPACIDADE -> nivelCapacidade = nivel;
            case EXCLUSIVA -> exclusiva = nivel > 1;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Maleta getMaleta() { return maleta; }
    public void setMaleta(Maleta maleta) { this.maleta = maleta; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public Integer getPosicaoX() { return posicaoX; }
    public void setPosicaoX(Integer posicaoX) { this.posicaoX = posicaoX; }
    public Integer getPosicaoY() { return posicaoY; }
    public void setPosicaoY(Integer posicaoY) { this.posicaoY = posicaoY; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public boolean isRotacionado() { return rotacionado; }
    public void setRotacionado(boolean rotacionado) { this.rotacionado = rotacionado; }
    public boolean isEquipada() { return equipada; }
    public void setEquipada(boolean equipada) { this.equipada = equipada; }
    public Integer getMunicaoCarregada() { return municaoCarregada; }
    public void setMunicaoCarregada(Integer municaoCarregada) { this.municaoCarregada = municaoCarregada; }
    public Integer getNivelPoder() { return nivelPoder; }
    public void setNivelPoder(Integer nivelPoder) { this.nivelPoder = nivelPoder; }
    public Integer getNivelCadencia() { return nivelCadencia; }
    public void setNivelCadencia(Integer nivelCadencia) { this.nivelCadencia = nivelCadencia; }
    public Integer getNivelRecarga() { return nivelRecarga; }
    public void setNivelRecarga(Integer nivelRecarga) { this.nivelRecarga = nivelRecarga; }
    public Integer getNivelCapacidade() { return nivelCapacidade; }
    public void setNivelCapacidade(Integer nivelCapacidade) { this.nivelCapacidade = nivelCapacidade; }
    public boolean isExclusiva() { return exclusiva; }
    public void setExclusiva(boolean exclusiva) { this.exclusiva = exclusiva; }
    public Integer getInvestido() { return investido; }
    public void setInvestido(Integer investido) { this.investido = investido; }
}
