package com.faculdade.inventario.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "tamanho_maleta")
public class TamanhoMaleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(max = 40)
    @Column(nullable = false, unique = true, length = 40)
    private String nome;

    @NotNull(message = "Informe as colunas")
    @Min(value = 4, message = "Minimo 4")
    @Max(value = 20, message = "Maximo 20")
    @Column(nullable = false)
    private Integer colunas = 10;

    @NotNull(message = "Informe as linhas")
    @Min(value = 4, message = "Minimo 4")
    @Max(value = 15, message = "Maximo 15")
    @Column(nullable = false)
    private Integer linhas = 6;

    @NotNull(message = "Informe o preco")
    @PositiveOrZero
    @Column(nullable = false)
    private Integer preco = 0;

    @NotNull(message = "Informe a ordem")
    @Min(1)
    @Column(nullable = false)
    private Integer ordem = 1;

    public TamanhoMaleta() {
    }

    public TamanhoMaleta(String nome, int colunas, int linhas, int preco, int ordem) {
        this.nome = nome;
        this.colunas = colunas;
        this.linhas = linhas;
        this.preco = preco;
        this.ordem = ordem;
    }

    public int getCapacidade() {
        return colunas * linhas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getColunas() { return colunas; }
    public void setColunas(Integer colunas) { this.colunas = colunas; }
    public Integer getLinhas() { return linhas; }
    public void setLinhas(Integer linhas) { this.linhas = linhas; }
    public Integer getPreco() { return preco; }
    public void setPreco(Integer preco) { this.preco = preco; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
}
