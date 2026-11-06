package com.faculdade.inventario.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "item")
public class Item {

    public static final int CURA_TOTAL = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do item")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String nome;

    @Size(max = 500)
    @Column(length = 500)
    private String descricao;

    @NotNull(message = "Informe a largura")
    @Min(value = 1, message = "Largura minima 1")
    @Max(value = 13, message = "Largura maxima 13")
    @Column(nullable = false)
    private Integer largura = 1;

    @NotNull(message = "Informe a altura")
    @Min(value = 1, message = "Altura minima 1")
    @Max(value = 9, message = "Altura maxima 9")
    @Column(nullable = false)
    private Integer altura = 1;

    @NotNull(message = "Informe o preco")
    @PositiveOrZero(message = "Preco nao pode ser negativo")
    @Column(nullable = false)
    private Integer preco = 0;

    @Column(name = "vendido_mercador", nullable = false)
    private boolean vendidoMercador = false;

    @NotNull(message = "Informe o valor de venda")
    @PositiveOrZero(message = "Valor nao pode ser negativo")
    @Column(name = "valor_venda", nullable = false)
    private Integer valorVenda = 0;

    @NotNull(message = "Informe o empilhamento maximo")
    @Min(value = 1, message = "Minimo 1")
    @Column(name = "empilhamento_maximo", nullable = false)
    private Integer empilhamentoMaximo = 1;

    @NotNull(message = "Selecione uma categoria")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "municao_id")
    private Item municao;

    @Min(value = -1, message = "Use -1 para cura total")
    private Integer cura;

    @PositiveOrZero
    @Column(name = "aumento_vida_maxima")
    private Integer aumentoVidaMaxima;

    @Size(max = 255)
    @Pattern(regexp = "^$|^/(img|uploads)/[A-Za-z0-9._/-]+$",
            message = "Use um caminho como /img/itens/pistola.svg ou envie um arquivo")
    private String imagem;

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = (imagem == null || imagem.isBlank()) ? null : imagem.trim(); }

    public Item() {
    }

    public Item(String nome, String descricao, int largura, int altura, int preco, boolean vendidoMercador,
                int valorVenda, int empilhamentoMaximo, Categoria categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.largura = largura;
        this.altura = altura;
        this.preco = preco;
        this.vendidoMercador = vendidoMercador;
        this.valorVenda = valorVenda;
        this.empilhamentoMaximo = empilhamentoMaximo;
        this.categoria = categoria;
    }

    public boolean isArma() {
        return categoria != null && categoria.getTipo() == TipoCategoria.ARMA;
    }

    public boolean isUsavel() {
        return cura != null;
    }

    public boolean isOcupaMaleta() {
        return categoria == null || categoria.getTipo().isOcupaMaleta();
    }

    public int getArea() {
        return largura * altura;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getLargura() { return largura; }
    public void setLargura(Integer largura) { this.largura = largura; }
    public Integer getAltura() { return altura; }
    public void setAltura(Integer altura) { this.altura = altura; }
    public Integer getPreco() { return preco; }
    public void setPreco(Integer preco) { this.preco = preco; }
    public boolean isVendidoMercador() { return vendidoMercador; }
    public void setVendidoMercador(boolean vendidoMercador) { this.vendidoMercador = vendidoMercador; }
    public Integer getValorVenda() { return valorVenda; }
    public void setValorVenda(Integer valorVenda) { this.valorVenda = valorVenda; }
    public Integer getEmpilhamentoMaximo() { return empilhamentoMaximo; }
    public void setEmpilhamentoMaximo(Integer empilhamentoMaximo) { this.empilhamentoMaximo = empilhamentoMaximo; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Item getMunicao() { return municao; }
    public void setMunicao(Item municao) { this.municao = municao; }
    public Integer getCura() { return cura; }
    public void setCura(Integer cura) { this.cura = cura; }
    public Integer getAumentoVidaMaxima() { return aumentoVidaMaxima; }
    public void setAumentoVidaMaxima(Integer aumentoVidaMaxima) { this.aumentoVidaMaxima = aumentoVidaMaxima; }
}
