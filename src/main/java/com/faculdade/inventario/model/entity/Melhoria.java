package com.faculdade.inventario.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "melhoria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"arma_id", "atributo", "nivel"}))
public class Melhoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Selecione a arma")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "arma_id", nullable = false)
    private Item arma;

    @NotNull(message = "Selecione o atributo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AtributoArma atributo;

    @NotNull(message = "Informe o nivel")
    @Min(value = 1, message = "Nivel minimo 1")
    @Max(value = 10, message = "Nivel maximo 10")
    @Column(nullable = false)
    private Integer nivel;

    private Double valor;

    @NotNull(message = "Informe o custo")
    @PositiveOrZero(message = "Custo nao pode ser negativo")
    @Column(nullable = false)
    private Integer custo = 0;

    @Size(max = 200)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "atributo_afetado", length = 20)
    private AtributoArma atributoAfetado;

    public AtributoArma getAtributoAfetado() { return atributoAfetado; }
    public void setAtributoAfetado(AtributoArma atributoAfetado) { this.atributoAfetado = atributoAfetado; }

    public Melhoria() {
    }

    public Melhoria(Item arma, AtributoArma atributo, int nivel, Double valor, int custo, String descricao) {
        this.arma = arma;
        this.atributo = atributo;
        this.nivel = nivel;
        this.valor = valor;
        this.custo = custo;
        this.descricao = descricao;
    }

    public String getValorFormatado() {
        return atributo == null ? "" : atributo.formatar(valor);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Item getArma() { return arma; }
    public void setArma(Item arma) { this.arma = arma; }
    public AtributoArma getAtributo() { return atributo; }
    public void setAtributo(AtributoArma atributo) { this.atributo = atributo; }
    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public Integer getCusto() { return custo; }
    public void setCusto(Integer custo) { this.custo = custo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
