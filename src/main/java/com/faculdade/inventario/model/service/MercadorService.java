package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.*;
import com.faculdade.inventario.model.repository.ItemMaletaRepository;
import com.faculdade.inventario.model.repository.MaletaRepository;
import com.faculdade.inventario.model.repository.PersonagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MercadorService {

    private final ItemMaletaService inventario;
    private final ItemService itemService;
    private final MelhoriaService melhoriaService;
    private final TamanhoMaletaService tamanhoService;
    private final TransacaoService transacaoService;
    private final MaletaRepository maletaRepository;
    private final PersonagemRepository personagemRepository;
    private final ItemMaletaRepository itemMaletaRepository;

    public MercadorService(ItemMaletaService inventario, ItemService itemService, MelhoriaService melhoriaService,
                           TamanhoMaletaService tamanhoService, TransacaoService transacaoService,
                           MaletaRepository maletaRepository, PersonagemRepository personagemRepository,
                           ItemMaletaRepository itemMaletaRepository) {
        this.inventario = inventario;
        this.itemService = itemService;
        this.melhoriaService = melhoriaService;
        this.tamanhoService = tamanhoService;
        this.transacaoService = transacaoService;
        this.maletaRepository = maletaRepository;
        this.personagemRepository = personagemRepository;
        this.itemMaletaRepository = itemMaletaRepository;
    }

    @Transactional
    public String comprar(Long maletaId, Long itemId) {
        Maleta maleta = maleta(maletaId);
        Item item = itemService.buscarPorId(itemId);
        if (!item.isVendidoMercador()) {
            throw new RegraNegocioException("O mercador nao vende \"" + item.getNome() + "\".");
        }
        Personagem p = maleta.getPersonagem();
        debitar(p, item.getPreco());
        if (!inventario.temEspaco(maleta, item)) {
            throw new RegraNegocioException("Nao ha espaco na maleta para " + item.getNome() + " ("
                    + item.getLargura() + "x" + item.getAltura() + "). Reorganize ou venda algo antes.");
        }
        ItemMaleta novo = inventario.adicionar(maleta, item, 1);
        novo.setInvestido(item.getPreco());
        itemMaletaRepository.save(novo);
        transacaoService.registrar(p, TipoTransacao.COMPRA, item.getNome(), -item.getPreco());
        return "Comprou " + item.getNome() + " por " + formatar(item.getPreco()) + ".";
    }

    @Transactional
    public String vender(Long itemMaletaId) {
        ItemMaleta im = inventario.buscarPorId(itemMaletaId);
        int valor = valorVenda(im);
        if (im.getItem().getCategoria().getTipo() == TipoCategoria.CHAVE) {
            throw new RegraNegocioException("Itens-chave nao podem ser vendidos.");
        }
        Personagem p = im.getMaleta().getPersonagem();
        p.setPesetas(p.getPesetas() + valor);
        personagemRepository.save(p);
        String descricao = im.getItem().getNome() + (im.getQuantidade() > 1 ? " x" + im.getQuantidade() : "");
        itemMaletaRepository.delete(im);
        transacaoService.registrar(p, TipoTransacao.VENDA, descricao, valor);
        return "Vendeu " + descricao + " por " + formatar(valor) + ".";
    }

    @Transactional
    public String melhorar(Long itemMaletaId, AtributoArma atributo) {
        ItemMaleta arma = inventario.buscarPorId(itemMaletaId);
        if (!arma.getItem().isArma()) {
            throw new RegraNegocioException("So armas podem ser melhoradas.");
        }
        if (atributo == AtributoArma.EXCLUSIVA) {
            if (arma.isExclusiva()) {
                throw new RegraNegocioException("A melhoria exclusiva ja foi comprada.");
            }
            if (!melhoriaService.tudoNoMaximo(arma)) {
                throw new RegraNegocioException("A melhoria exclusiva so fica disponivel com todos os atributos no maximo.");
            }
        }
        Melhoria prox = melhoriaService.proximoNivel(arma, atributo)
                .orElseThrow(() -> new RegraNegocioException(atributo.getRotulo() + " de "
                        + arma.getItem().getNome() + " ja esta no nivel maximo."));
        Personagem p = arma.getMaleta().getPersonagem();
        debitar(p, prox.getCusto());
        arma.setNivel(atributo, prox.getNivel());
        arma.setInvestido(arma.getInvestido() + prox.getCusto());
        itemMaletaRepository.save(arma);

        String desc = arma.getItem().getNome() + " · " + atributo.getRotulo()
                + (atributo == AtributoArma.EXCLUSIVA ? "" : " nv." + prox.getNivel());
        transacaoService.registrar(p, TipoTransacao.MELHORIA, desc, -prox.getCusto());
        return "Melhorou " + desc + " por " + formatar(prox.getCusto()) + ".";
    }

    @Transactional
    public String comprarMaleta(Long maletaId, Long tamanhoId) {
        Maleta maleta = maleta(maletaId);
        TamanhoMaleta novo = tamanhoService.buscarPorId(tamanhoId);
        if (novo.getOrdem() <= maleta.getTamanho().getOrdem()) {
            throw new RegraNegocioException("So e possivel trocar por uma maleta maior que a atual.");
        }
        Personagem p = maleta.getPersonagem();
        debitar(p, novo.getPreco());
        maleta.setTamanho(novo);
        maletaRepository.save(maleta);
        transacaoService.registrar(p, TipoTransacao.MALETA, novo.getNome(), -novo.getPreco());
        return "Maleta trocada por " + novo.getNome() + " (" + novo.getColunas() + "x" + novo.getLinhas() + ").";
    }

    @Transactional(readOnly = true)
    public int valorVenda(ItemMaleta im) {
        Item item = im.getItem();
        return switch (item.getCategoria().getTipo()) {
            case CHAVE -> 0;
            case ARMA -> im.getInvestido() > 0 ? im.getInvestido() / 2 : item.getPreco() / 2;
            default -> item.getValorVenda() * im.getQuantidade();
        };
    }

    private Maleta maleta(Long id) {
        return maletaRepository.findById(id).orElseThrow(() -> new RegraNegocioException("Maleta nao encontrada."));
    }

    private void debitar(Personagem p, int valor) {
        if (p.getPesetas() < valor) {
            throw new RegraNegocioException("Pesetas insuficientes: custa " + formatar(valor) + " e "
                    + p.getNome() + " tem " + formatar(p.getPesetas()) + ".");
        }
        p.setPesetas(p.getPesetas() - valor);
        personagemRepository.save(p);
    }

    static String formatar(int pesetas) {
        return String.format(java.util.Locale.forLanguageTag("pt-BR"), "₧ %,d", pesetas);
    }
}
