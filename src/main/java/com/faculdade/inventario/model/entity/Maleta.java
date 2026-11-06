package com.faculdade.inventario.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maleta")
public class Maleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome da maleta")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String nome;

    @NotNull(message = "Selecione o tamanho")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "tamanho_id", nullable = false)
    private TamanhoMaleta tamanho;

    @NotNull(message = "Selecione o personagem dono")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "personagem_id", nullable = false)
    private Personagem personagem;

    @OneToMany(mappedBy = "maleta", cascade = CascadeType.REMOVE)
    private List<ItemMaleta> itens = new ArrayList<>();

    public Maleta() {
    }

    public Maleta(String nome, TamanhoMaleta tamanho, Personagem personagem) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.personagem = personagem;
    }

    public int getColunas() {
        return tamanho.getColunas();
    }

    public int getLinhas() {
        return tamanho.getLinhas();
    }

    public int getCapacidade() {
        return tamanho.getCapacidade();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TamanhoMaleta getTamanho() { return tamanho; }
    public void setTamanho(TamanhoMaleta tamanho) { this.tamanho = tamanho; }
    public Personagem getPersonagem() { return personagem; }
    public void setPersonagem(Personagem personagem) { this.personagem = personagem; }
    public List<ItemMaleta> getItens() { return itens; }
    public void setItens(List<ItemMaleta> itens) { this.itens = itens; }
}
