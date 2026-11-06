package com.faculdade.inventario.config;

import com.faculdade.inventario.model.entity.*;
import com.faculdade.inventario.model.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.faculdade.inventario.model.entity.AtributoArma.*;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final CategoriaService categoriaService;
    private final ItemService itemService;
    private final MelhoriaService melhoriaService;
    private final TamanhoMaletaService tamanhoService;
    private final CombinacaoService combinacaoService;
    private final PersonagemService personagemService;
    private final MaletaService maletaService;
    private final ItemMaletaService inventario;

    public DadosIniciais(CategoriaService categoriaService, ItemService itemService, MelhoriaService melhoriaService,
                         TamanhoMaletaService tamanhoService, CombinacaoService combinacaoService,
                         PersonagemService personagemService, MaletaService maletaService,
                         ItemMaletaService inventario) {
        this.categoriaService = categoriaService;
        this.itemService = itemService;
        this.melhoriaService = melhoriaService;
        this.tamanhoService = tamanhoService;
        this.combinacaoService = combinacaoService;
        this.personagemService = personagemService;
        this.maletaService = maletaService;
        this.inventario = inventario;
    }

    private static final Map<String, String> ICONES = Map.ofEntries(
            Map.entry("Handgun", "/img/itens/pistola.svg"),
            Map.entry("Punisher", "/img/itens/pistola-punisher.svg"),
            Map.entry("Red9", "/img/itens/red9.svg"),
            Map.entry("Blacktail", "/img/itens/pistola-blacktail.svg"),
            Map.entry("Shotgun", "/img/itens/escopeta.svg"),
            Map.entry("Riot Gun", "/img/itens/escopeta-riot.svg"),
            Map.entry("Striker", "/img/itens/striker.svg"),
            Map.entry("Rifle", "/img/itens/rifle.svg"),
            Map.entry("Rifle semiautomático", "/img/itens/rifle-semi.svg"),
            Map.entry("TMP", "/img/itens/tmp.svg"),
            Map.entry("Broken Butterfly", "/img/itens/revolver.svg"),
            Map.entry("Killer7", "/img/itens/pistola-killer7.svg"),
            Map.entry("Lança-mísseis", "/img/itens/lanca-misseis.svg"),
            Map.entry("Munição de pistola", "/img/itens/municao-pistola.svg"),
            Map.entry("Cartuchos de escopeta", "/img/itens/cartuchos.svg"),
            Map.entry("Munição de rifle", "/img/itens/municao-rifle.svg"),
            Map.entry("Munição de TMP", "/img/itens/municao-tmp.svg"),
            Map.entry("Munição de magnum", "/img/itens/municao-magnum.svg"),
            Map.entry("Erva verde", "/img/itens/erva-verde.svg"),
            Map.entry("Erva vermelha", "/img/itens/erva-vermelha.svg"),
            Map.entry("Erva amarela", "/img/itens/erva-amarela.svg"),
            Map.entry("Mistura de ervas (V+V)", "/img/itens/mistura-vv.svg"),
            Map.entry("Mistura de ervas (V+V+V)", "/img/itens/mistura-vvv.svg"),
            Map.entry("Mistura de ervas (V+Vm)", "/img/itens/mistura-vvm.svg"),
            Map.entry("Mistura de ervas (V+A)", "/img/itens/mistura-va.svg"),
            Map.entry("Mistura de ervas (Vm+A)", "/img/itens/mistura-vma.svg"),
            Map.entry("Mistura de ervas (V+Vm+A)", "/img/itens/mistura-vvma.svg"),
            Map.entry("Spray de primeiros socorros", "/img/itens/spray.svg"),
            Map.entry("Ovo de galinha", "/img/itens/ovo.svg"),
            Map.entry("Ovo marrom", "/img/itens/ovo-marrom.svg"),
            Map.entry("Ovo de ouro", "/img/itens/ovo-ouro.svg"),
            Map.entry("Peixe", "/img/itens/peixe.svg"),
            Map.entry("Granada de mão", "/img/itens/granada.svg"),
            Map.entry("Granada incendiária", "/img/itens/granada-incendiaria.svg"),
            Map.entry("Granada de luz", "/img/itens/granada-luz.svg"),
            Map.entry("Espinélio", "/img/itens/espinelio.svg"),
            Map.entry("Veludo azul", "/img/itens/veludo-azul.svg"),
            Map.entry("Colar de pérolas", "/img/itens/colar-perolas.svg"),
            Map.entry("Máscara elegante", "/img/itens/mascara.svg"),
            Map.entry("Ampulheta dourada", "/img/itens/ampulheta.svg"));

    @Override
    public void run(String... args) {
        if (categoriaService.contar() == 0) {
            popular();
        }
        itemService.definirImagensPadrao(ICONES);
    }

    private void popular() {

        TamanhoMaleta p = tamanhoService.salvar(new TamanhoMaleta("Maleta P", 10, 6, 0, 1));
        TamanhoMaleta m = tamanhoService.salvar(new TamanhoMaleta("Maleta M", 10, 8, 24000, 2));
        tamanhoService.salvar(new TamanhoMaleta("Maleta G", 13, 8, 48000, 3));
        tamanhoService.salvar(new TamanhoMaleta("Maleta GG", 13, 9, 73000, 4));

        Categoria arma = categoriaService.salvar(new Categoria("Armas", "Pistolas, escopetas, rifles, submetralhadoras e magnums", TipoCategoria.ARMA, "#56606b"));
        Categoria mun = categoriaService.salvar(new Categoria("Munição", "Caixas de munição (empilham)", TipoCategoria.MUNICAO, "#8a6d2f"));
        Categoria cura = categoriaService.salvar(new Categoria("Recuperação", "Ervas, sprays e alimentos", TipoCategoria.CURA, "#3f7d3a"));
        Categoria gran = categoriaService.salvar(new Categoria("Granadas", "Explosivos de arremesso", TipoCategoria.GRANADA, "#8b3a3a"));
        Categoria tes = categoriaService.salvar(new Categoria("Tesouros", "Joias e relíquias para vender ao mercador", TipoCategoria.TESOURO, "#b08f2e"));
        categoriaService.salvar(new Categoria("Itens-chave", "Chaves e objetos de história", TipoCategoria.CHAVE, "#6a4c8c"));

        Item mPistola = item("Munição de pistola", "Balas 9mm para pistolas.", 2, 1, 0, false, 3, 50, mun);
        Item mEscopeta = item("Cartuchos de escopeta", "Cartuchos calibre 12.", 2, 1, 0, false, 20, 15, mun);
        Item mRifle = item("Munição de rifle", "Balas de alto calibre para rifles.", 2, 1, 0, false, 60, 10, mun);
        Item mTmp = item("Munição de TMP", "Pente para submetralhadora.", 2, 1, 0, false, 2, 100, mun);
        Item mMagnum = item("Munição de magnum", "Balas de magnum, raras e poderosas.", 2, 1, 0, false, 150, 10, mun);

        Item handgun = arma("Handgun", "Pistola semiautomática 9mm padrão.", 3, 2, 8000, arma, mPistola);
        niveis(handgun, PODER, d(1.0, 1.2, 1.4, 1.6, 1.8, 2.0), c(0, 7000, 10000, 15000, 18000, 20000));
        niveis(handgun, CADENCIA, d(0.47, 0.40), c(0, 5000));
        niveis(handgun, RECARGA, d(1.73, 0.87), c(0, 4000));
        niveis(handgun, CAPACIDADE, d(10, 13, 16, 19, 22, 25), c(0, 4000, 6000, 8000, 10000, 12000));
        exclusiva(handgun, 57000, "Chance de tiro crítico na cabeça x5", null, null);

        Item punisher = arma("Punisher", "Pistola capaz de atravessar alvos.", 3, 2, 20000, arma, mPistola);
        niveis(punisher, PODER, d(0.9, 1.1, 1.3, 1.5, 1.7, 1.9), c(0, 10000, 15000, 20000, 25000, 35000));
        niveis(punisher, CADENCIA, d(0.47, 0.40), c(0, 10000));
        niveis(punisher, RECARGA, d(1.70, 0.85), c(0, 15000));
        niveis(punisher, CAPACIDADE, d(10, 13, 16, 20, 24, 28), c(0, 8000, 10000, 15000, 18000, 25000));
        exclusiva(punisher, 40000, "Atravessa até 5 inimigos", null, null);

        Item red9 = arma("Red9", "Pistola de alto poder com coronha opcional.", 5, 2, 14000, arma, mPistola);
        niveis(red9, PODER, d(1.6, 1.8, 2.1, 2.4, 2.8, 3.3), c(0, 15000, 20000, 24000, 28000, 45000));
        niveis(red9, CADENCIA, d(0.53, 0.47), c(0, 15000));
        niveis(red9, RECARGA, d(2.37, 1.18), c(0, 6000));
        niveis(red9, CAPACIDADE, d(8, 10, 12, 15, 18, 22), c(0, 10000, 12000, 15000, 20000, 25000));
        exclusiva(red9, 80000, "Poder de fogo sobe para 6.5", PODER, 6.5);

        Item blacktail = arma("Blacktail", "Pistola rápida e equilibrada.", 3, 2, 24000, arma, mPistola);
        niveis(blacktail, PODER, d(1.6, 1.8, 2.0, 2.4, 2.8, 3.0), c(0, 18000, 24000, 30000, 40000, 45000));
        niveis(blacktail, CADENCIA, d(0.27, 0.23), c(0, 15000));
        niveis(blacktail, RECARGA, d(1.47, 0.73), c(0, 8000));
        niveis(blacktail, CAPACIDADE, d(15, 18, 21, 25, 30, 35), c(0, 8000, 10000, 12000, 15000, 20000));
        exclusiva(blacktail, 80000, "Poder de fogo sobe para 3.4", PODER, 3.4);

        Item shotgun = arma("Shotgun", "Escopeta de cano longo, devastadora a curta distância.", 8, 2, 20000, arma, mEscopeta);
        niveis(shotgun, PODER, d(4.0, 4.5, 5.0, 6.0, 7.0, 8.0), c(0, 15000, 20000, 25000, 35000, 50000));
        niveis(shotgun, CADENCIA, d(1.53), c(0));
        niveis(shotgun, RECARGA, d(3.03, 1.53), c(0, 7000));
        niveis(shotgun, CAPACIDADE, d(6, 8, 10, 12, 15, 18), c(0, 8000, 10000, 12000, 15000, 20000));
        exclusiva(shotgun, 90000, "Poder de fogo x2 e maior alcance", PODER, 16.0);

        Item riot = arma("Riot Gun", "Escopeta policial com dispersão ampla.", 8, 2, 32000, arma, mEscopeta);
        niveis(riot, PODER, d(5.0, 5.5, 6.0, 6.5, 7.0, 8.0), c(0, 20000, 25000, 30000, 40000, 50000));
        niveis(riot, CADENCIA, d(1.53), c(0));
        niveis(riot, RECARGA, d(3.03, 1.53), c(0, 20000));
        niveis(riot, CAPACIDADE, d(7, 9, 11, 13, 15, 17), c(0, 10000, 12000, 15000, 18000, 20000));
        exclusiva(riot, 120000, "Poder de fogo sobe para 10.0", PODER, 10.0);

        Item striker = arma("Striker", "Escopeta semiautomática de tambor.", 5, 2, 43000, arma, mEscopeta);
        niveis(striker, PODER, d(6.0, 6.5, 7.0, 7.5, 8.0), c(0, 25000, 30000, 40000, 50000));
        niveis(striker, CADENCIA, d(0.33), c(0));
        niveis(striker, RECARGA, d(3.00), c(0));
        niveis(striker, CAPACIDADE, d(12, 14, 16, 20, 24, 28), c(0, 10000, 12000, 15000, 18000, 20000));
        exclusiva(striker, 60000, "Capacidade sobe para 100", CAPACIDADE, 100.0);

        Item rifle = arma("Rifle", "Rifle de ferrolho com luneta.", 9, 2, 12000, arma, mRifle);
        niveis(rifle, PODER, d(4.0, 5.0, 6.0, 8.0, 10.0, 12.0), c(0, 10000, 12000, 20000, 25000, 35000));
        niveis(rifle, CADENCIA, d(2.73, 2.33, 1.47), c(0, 5000, 12000));
        niveis(rifle, RECARGA, d(4.00, 2.33, 1.33), c(0, 6000, 12000));
        niveis(rifle, CAPACIDADE, d(5, 7, 9, 10, 12), c(0, 4000, 8000, 10000, 12000));
        exclusiva(rifle, 80000, "Poder de fogo sobe para 30.0", PODER, 30.0);

        Item semi = arma("Rifle semiautomático", "Rifle de disparo rápido com luneta.", 9, 2, 35000, arma, mRifle);
        niveis(semi, PODER, d(7.0, 8.0, 9.0, 10.0, 12.0, 15.0), c(0, 15000, 18000, 20000, 25000, 35000));
        niveis(semi, CADENCIA, d(1.17, 0.80), c(0, 20000));
        niveis(semi, RECARGA, d(2.33, 1.17), c(0, 15000));
        niveis(semi, CAPACIDADE, d(10, 12, 15, 20), c(0, 10000, 12000, 15000));
        exclusiva(semi, 80000, "Poder de fogo sobe para 30.0", PODER, 30.0);

        Item tmp = arma("TMP", "Submetralhadora com alta cadência.", 4, 2, 10000, arma, mTmp);
        niveis(tmp, PODER, d(0.4, 0.5, 0.6, 0.8, 1.0, 1.2), c(0, 7000, 14000, 18000, 24000, 35000));
        niveis(tmp, CADENCIA, d(0.10), c(0));
        niveis(tmp, RECARGA, d(1.70, 0.85), c(0, 4000));
        niveis(tmp, CAPACIDADE, d(30, 50, 100, 150, 200, 250), c(0, 4000, 7000, 10000, 15000, 20000));
        exclusiva(tmp, 35000, "Poder de fogo sobe para 1.8", PODER, 1.8);

        Item butterfly = arma("Broken Butterfly", "Revólver magnum de poder extremo.", 6, 2, 38000, arma, mMagnum);
        niveis(butterfly, PODER, d(13.0, 15.0, 17.0, 20.0, 24.0, 27.0), c(0, 25000, 30000, 35000, 50000, 70000));
        niveis(butterfly, CADENCIA, d(0.70), c(0));
        niveis(butterfly, RECARGA, d(3.67, 2.20), c(0, 20000));
        niveis(butterfly, CAPACIDADE, d(6, 8, 10, 12), c(0, 15000, 20000, 25000));
        exclusiva(butterfly, 150000, "Poder de fogo sobe para 50.0", PODER, 50.0);

        Item killer7 = arma("Killer7", "Magnum semiautomática. Não pode ser melhorada.", 4, 2, 77700, arma, mMagnum);
        niveis(killer7, PODER, d(25.0), c(0));
        niveis(killer7, CADENCIA, d(0.70), c(0));
        niveis(killer7, RECARGA, d(1.83), c(0));
        niveis(killer7, CAPACIDADE, d(7), c(0));

        arma("Lança-mísseis", "Disparo único. Descartado após o uso.", 8, 2, 30000, arma, null);

        Item verde = cura("Erva verde", "Recupera um pouco de vida.", 1, 2, 0, false, 100, 250, null, cura);
        Item vermelha = cura("Erva vermelha", "Sozinha não tem efeito. Combine com uma erva verde.", 1, 2, 0, false, 100, null, null, cura);
        Item amarela = cura("Erva amarela", "Sozinha não tem efeito. Combinada, aumenta a vida máxima.", 1, 2, 0, false, 100, null, null, cura);
        Item vv = cura("Mistura de ervas (V+V)", "Recupera bastante vida.", 1, 2, 0, false, 200, 600, null, cura);
        Item vvv = cura("Mistura de ervas (V+V+V)", "Recupera toda a vida.", 1, 2, 0, false, 300, Item.CURA_TOTAL, null, cura);
        Item vvm = cura("Mistura de ervas (V+Vm)", "Recupera toda a vida.", 1, 2, 0, false, 300, Item.CURA_TOTAL, null, cura);
        Item va = cura("Mistura de ervas (V+A)", "Recupera vida e aumenta a vida máxima.", 1, 2, 0, false, 300, 250, 100, cura);
        Item vma = cura("Mistura de ervas (Vm+A)", "Sozinha não tem efeito. Combine com uma erva verde.", 1, 2, 0, false, 300, null, null, cura);
        Item vvma = cura("Mistura de ervas (V+Vm+A)", "Recupera toda a vida e aumenta a vida máxima.", 1, 2, 0, false, 500, Item.CURA_TOTAL, 150, cura);
        Item spray = cura("Spray de primeiros socorros", "Recupera toda a vida.", 1, 2, 3000, true, 1500, Item.CURA_TOTAL, null, cura);
        cura("Ovo de galinha", "Recupera um pouco de vida.", 1, 1, 0, false, 20, 100, null, cura);
        cura("Ovo marrom", "Recupera vida.", 1, 1, 0, false, 50, 250, null, cura);
        cura("Ovo de ouro", "Recupera toda a vida.", 1, 1, 0, false, 200, Item.CURA_TOTAL, null, cura);
        cura("Peixe", "Recupera bastante vida.", 2, 1, 0, false, 100, 400, null, cura);

        combinacaoService.salvar(new Combinacao(verde, verde, vv));
        combinacaoService.salvar(new Combinacao(verde, vermelha, vvm));
        combinacaoService.salvar(new Combinacao(verde, amarela, va));
        combinacaoService.salvar(new Combinacao(vermelha, amarela, vma));
        combinacaoService.salvar(new Combinacao(vv, verde, vvv));
        combinacaoService.salvar(new Combinacao(vvm, amarela, vvma));
        combinacaoService.salvar(new Combinacao(va, vermelha, vvma));
        combinacaoService.salvar(new Combinacao(vma, verde, vvma));

        Item granada = item("Granada de mão", "Explode após alguns segundos.", 1, 2, 1000, true, 500, 1, gran);
        item("Granada incendiária", "Espalha fogo na área.", 1, 2, 800, true, 400, 1, gran);
        Item flash = item("Granada de luz", "Cega os inimigos próximos.", 1, 2, 1000, true, 500, 1, gran);

        Item espinelio = item("Espinélio", "Pedra preciosa vermelha.", 1, 1, 0, false, 2000, 1, tes);
        item("Veludo azul", "Joia de um azul profundo.", 1, 1, 0, false, 2500, 1, tes);
        Item colar = item("Colar de pérolas", "Colar de pérolas brancas.", 1, 1, 0, false, 10000, 1, tes);
        item("Máscara elegante", "Máscara ornamentada (sem as gemas).", 1, 1, 0, false, 3000, 1, tes);
        item("Ampulheta dourada", "Relógio de areia folheado a ouro.", 1, 1, 0, false, 12000, 1, tes);

        Personagem leon = personagemService.salvar(new Personagem("Leon S. Kennedy", "Agente do governo", 60000, 640, 1000));
        Personagem ada = personagemService.salvar(new Personagem("Ada Wong", "Espiã", 35000, 1000, 1000));

        Maleta mLeon = maletaService.salvar(new Maleta("Maleta do Leon", m, leon));
        ItemMaleta hg = inventario.adicionar(mLeon, handgun, 1);
        inventario.adicionar(mLeon, shotgun, 1);
        inventario.adicionar(mLeon, mPistola, 72);
        inventario.adicionar(mLeon, mEscopeta, 10);
        inventario.adicionar(mLeon, verde, 1);
        inventario.adicionar(mLeon, verde, 1);
        inventario.adicionar(mLeon, vermelha, 1);
        inventario.adicionar(mLeon, amarela, 1);
        inventario.adicionar(mLeon, spray, 1);
        inventario.adicionar(mLeon, granada, 1);
        inventario.adicionar(mLeon, flash, 1);
        inventario.adicionar(mLeon, espinelio, 3);
        inventario.adicionar(mLeon, colar, 1);
        inventario.equipar(hg.getId());
        inventario.recarregar(hg.getId());

        Maleta mAda = maletaService.salvar(new Maleta("Maleta da Ada", p, ada));
        inventario.adicionar(mAda, tmp, 1);
        inventario.adicionar(mAda, mTmp, 120);
        inventario.adicionar(mAda, verde, 1);
    }

    private Item item(String nome, String desc, int w, int h, int preco, boolean vende, int valorVenda,
                      int empilha, Categoria cat) {
        return itemService.salvar(new Item(nome, desc, w, h, preco, vende, valorVenda, empilha, cat));
    }

    private Item arma(String nome, String desc, int w, int h, int preco, Categoria cat, Item municao) {
        Item i = new Item(nome, desc, w, h, preco, true, 0, 1, cat);
        i.setMunicao(municao);
        return itemService.salvar(i);
    }

    private Item cura(String nome, String desc, int w, int h, int preco, boolean vende, int valorVenda,
                      Integer cura, Integer aumento, Categoria cat) {
        Item i = new Item(nome, desc, w, h, preco, vende, valorVenda, 1, cat);
        i.setCura(cura);
        i.setAumentoVidaMaxima(aumento);
        return itemService.salvar(i);
    }

    private void niveis(Item arma, AtributoArma atr, double[] valores, int[] custos) {
        for (int i = 0; i < valores.length; i++) {
            melhoriaService.salvar(new Melhoria(arma, atr, i + 1, valores[i], custos[i], null));
        }
    }

    private void exclusiva(Item arma, int custo, String descricao, AtributoArma afetado, Double valor) {
        Melhoria ex = new Melhoria(arma, EXCLUSIVA, 2, valor, custo, descricao);
        ex.setAtributoAfetado(afetado);
        melhoriaService.salvar(ex);
    }

    private static double[] d(double... v) {
        return v;
    }

    private static int[] c(int... v) {
        return v;
    }
}
