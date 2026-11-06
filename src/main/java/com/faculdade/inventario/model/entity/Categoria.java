package com.faculdade.inventario.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome da categoria")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Size(max = 255)
    private String descricao;

    @NotNull(message = "Selecione o tipo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCategoria tipo = TipoCategoria.ARMA;

    @NotBlank(message = "Informe uma cor")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Cor deve estar no formato #RRGGBB")
    @Column(nullable = false, length = 7)
    private String cor = "#6b8e23";

    public Categoria() {
    }

    public Categoria(String nome, String descricao, TipoCategoria tipo, String cor) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.cor = cor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public TipoCategoria getTipo() { return tipo; }
    public void setTipo(TipoCategoria tipo) { this.tipo = tipo; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}
