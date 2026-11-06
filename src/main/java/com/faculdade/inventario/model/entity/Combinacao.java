package com.faculdade.inventario.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "combinacao")
public class Combinacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Selecione o primeiro item")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_a_id", nullable = false)
    private Item itemA;

    @NotNull(message = "Selecione o segundo item")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_b_id", nullable = false)
    private Item itemB;

    @NotNull(message = "Selecione o resultado")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "resultado_id", nullable = false)
    private Item resultado;

    public Combinacao() {
    }

    public Combinacao(Item itemA, Item itemB, Item resultado) {
        this.itemA = itemA;
        this.itemB = itemB;
        this.resultado = resultado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Item getItemA() { return itemA; }
    public void setItemA(Item itemA) { this.itemA = itemA; }
    public Item getItemB() { return itemB; }
    public void setItemB(Item itemB) { this.itemB = itemB; }
    public Item getResultado() { return resultado; }
    public void setResultado(Item resultado) { this.resultado = resultado; }
}
