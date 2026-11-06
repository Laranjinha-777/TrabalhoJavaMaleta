package com.faculdade.inventario.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "personagem")
public class Personagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do personagem")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String nome;

    @Size(max = 80)
    @Column(length = 80)
    private String ocupacao;

    @NotNull(message = "Informe o saldo")
    @PositiveOrZero(message = "Saldo nao pode ser negativo")
    @Column(nullable = false)
    private Integer pesetas = 0;

    @NotNull(message = "Informe a vida")
    @PositiveOrZero(message = "Vida nao pode ser negativa")
    @Column(nullable = false)
    private Integer vida = 1000;

    @NotNull(message = "Informe a vida maxima")
    @Min(value = 1, message = "Vida maxima minima 1")
    @Column(name = "vida_maxima", nullable = false)
    private Integer vidaMaxima = 1000;

    public Personagem() {
    }

    public Personagem(String nome, String ocupacao, int pesetas, int vida, int vidaMaxima) {
        this.nome = nome;
        this.ocupacao = ocupacao;
        this.pesetas = pesetas;
        this.vida = vida;
        this.vidaMaxima = vidaMaxima;
    }

    public int getPercentualVida() {
        return vidaMaxima == 0 ? 0 : Math.min(100, vida * 100 / vidaMaxima);
    }

    public String getEstado() {
        int p = getPercentualVida();
        return p > 60 ? "fine" : p > 25 ? "caution" : "danger";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getOcupacao() { return ocupacao; }
    public void setOcupacao(String ocupacao) { this.ocupacao = ocupacao; }
    public Integer getPesetas() { return pesetas; }
    public void setPesetas(Integer pesetas) { this.pesetas = pesetas; }
    public Integer getVida() { return vida; }
    public void setVida(Integer vida) { this.vida = vida; }
    public Integer getVidaMaxima() { return vidaMaxima; }
    public void setVidaMaxima(Integer vidaMaxima) { this.vidaMaxima = vidaMaxima; }
}
