package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.*;
import com.faculdade.inventario.model.repository.ItemMaletaRepository;
import com.faculdade.inventario.model.repository.ItemRepository;
import com.faculdade.inventario.model.repository.MaletaRepository;
import com.faculdade.inventario.model.repository.PersonagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ItemMaletaService {

    private final ItemMaletaRepository itemMaletaRepository;
    private final MaletaRepository maletaRepository;
    private final ItemRepository itemRepository;
    private final PersonagemRepository personagemRepository;
    private final MelhoriaService melhoriaService;
    private final CombinacaoService combinacaoService;

    public ItemMaletaService(ItemMaletaRepository itemMaletaRepository, MaletaRepository maletaRepository,
                             ItemRepository itemRepository, PersonagemRepository personagemRepository,
                             MelhoriaService melhoriaService, CombinacaoService combinacaoService) {
        this.itemMaletaRepository = itemMaletaRepository;
        this.maletaRepository = maletaRepository;
        this.itemRepository = itemRepository;
        this.personagemRepository = personagemRepository;
        this.melhoriaService = melhoriaService;
        this.combinacaoService = combinacaoService;
    }

    @Transactional(readOnly = true)
    public List<ItemMaleta> listarTodos() {
        return itemMaletaRepository.findAllByOrderByMaletaNomeAscPosicaoYAscPosicaoXAsc();
    }

    @Transactional(readOnly = true)
    public List<ItemMaleta> listarPorMaleta(Long maletaId) {
        return itemMaletaRepository.findByMaletaIdOrderByPosicaoYAscPosicaoXAsc(maletaId);
    }

    @Transactional(readOnly = true)
    public List<ItemMaleta> listarGrade(Long maletaId) {
        return listarPorMaleta(maletaId).stream().filter(ItemMaleta::isNaGrade).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemMaleta> listarForaDaGrade(Long maletaId) {
        return listarPorMaleta(maletaId).stream().filter(im -> !im.isNaGrade()).toList();
    }

    @Transactional(readOnly = true)
    public ItemMaleta buscarPorId(Long id) {
        return itemMaletaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Item da maleta nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public long contar() {
        return itemMaletaRepository.count();
    }

    @Transactional(readOnly = true)
    public int municaoDisponivel(Long maletaId, Item municao) {
        if (municao == null) {
            return 0;
        }
        return itemMaletaRepository.findByMaletaIdAndItemIdOrderByQuantidadeAsc(maletaId, municao.getId())
                .stream().mapToInt(ItemMaleta::getQuantidade).sum();
    }

    @Transactional
    public ItemMaleta salvar(ItemMaleta im) {
        Maleta maleta = maletaRepository.findById(im.getMaleta().getId())
                .orElseThrow(() -> new RegraNegocioException("Maleta nao encontrada."));
        Item item = itemRepository.findById(im.getItem().getId())
                .orElseThrow(() -> new RegraNegocioException("Item nao encontrado."));
        im.setMaleta(maleta);
        im.setItem(item);

        if (im.getQuantidade() > item.getEmpilhamentoMaximo()) {
            throw new RegraNegocioException("\"" + item.getNome() + "\" empilha no maximo "
                    + item.getEmpilhamentoMaximo() + " por espaco.");
        }
        if (!item.isOcupaMaleta()) {
            im.setPosicaoX(null);
            im.setPosicaoY(null);
            im.setRotacionado(false);
        } else {
            if (im.getPosicaoX() == null || im.getPosicaoY() == null) {
                throw new RegraNegocioException("Informe a posicao (coluna e linha) do item na grade.");
            }
            validarPosicao(maleta, im.getId(), im.getPosicaoX(), im.getPosicaoY(),
                    im.getLarguraOcupada(), im.getAlturaOcupada(), item.getNome());
        }
        if (!item.isArma()) {
            im.setEquipada(false);
            im.setMunicaoCarregada(0);
        } else if (im.getId() != null) {
            ItemMaleta original = buscarPorId(im.getId());
            if (original.getItem().getId().equals(item.getId())) {
                im.setNivelPoder(original.getNivelPoder());
                im.setNivelCadencia(original.getNivelCadencia());
                im.setNivelRecarga(original.getNivelRecarga());
                im.setNivelCapacidade(original.getNivelCapacidade());
                im.setExclusiva(original.isExclusiva());
                im.setMunicaoCarregada(original.getMunicaoCarregada());
                im.setInvestido(original.getInvestido());
                im.setEquipada(original.isEquipada());
            }
        }
        return itemMaletaRepository.save(im);
    }

    @Transactional
    public ItemMaleta adicionar(Maleta maleta, Item item, int quantidade) {
        if (quantidade < 1) {
            throw new RegraNegocioException("Quantidade invalida.");
        }
        if (!item.isOcupaMaleta()) {
            return itemMaletaRepository.save(new ItemMaleta(maleta, item, null, null, quantidade, false));
        }
        ItemMaleta ultimo = null;
        int restante = quantidade;
        if (item.getEmpilhamentoMaximo() > 1) {
            for (ItemMaleta pilha : itemMaletaRepository.findByMaletaIdAndItemIdOrderByQuantidadeAsc(maleta.getId(), item.getId())) {
                int cabe = item.getEmpilhamentoMaximo() - pilha.getQuantidade();
                if (cabe > 0) {
                    int qtd = Math.min(cabe, restante);
                    pilha.setQuantidade(pilha.getQuantidade() + qtd);
                    restante -= qtd;
                    ultimo = itemMaletaRepository.save(pilha);
                }
                if (restante == 0) {
                    return ultimo;
                }
            }
        }
        while (restante > 0) {
            int qtd = Math.min(restante, item.getEmpilhamentoMaximo());
            int[] pos = encontrarEspaco(maleta, item, null);
            if (pos == null) {
                throw new RegraNegocioException("Nao ha espaco na maleta para \"" + item.getNome() + "\" ("
                        + item.getLargura() + "x" + item.getAltura() + "). Reorganize ou venda algo.");
            }
            ItemMaleta novo = new ItemMaleta(maleta, item, pos[0], pos[1], qtd, pos[2] == 1);
            if (item.isArma()) {
                novo.setInvestido(item.getPreco());
            }
            ultimo = itemMaletaRepository.save(novo);
            restante -= qtd;
        }
        return ultimo;
    }

    @Transactional
    public ItemMaleta guardarAutomaticamente(Long maletaId, Long itemId, int quantidade) {
        Maleta maleta = maletaRepository.findById(maletaId)
                .orElseThrow(() -> new RegraNegocioException("Maleta nao encontrada."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RegraNegocioException("Item nao encontrado."));
        return adicionar(maleta, item, quantidade);
    }

    @Transactional
    public ItemMaleta mover(Long id, int x, int y, boolean rotacionado) {
        ItemMaleta im = buscarPorId(id);
        if (!im.isNaGrade()) {
            throw new RegraNegocioException("Este item nao fica na grade da maleta.");
        }
        im.setRotacionado(rotacionado);
        validarPosicao(im.getMaleta(), im.getId(), x, y, im.getLarguraOcupada(), im.getAlturaOcupada(),
                im.getItem().getNome());
        im.setPosicaoX(x);
        im.setPosicaoY(y);
        return itemMaletaRepository.save(im);
    }

    @Transactional
    public void rotacionar(Long id) {
        ItemMaleta im = buscarPorId(id);
        mover(id, im.getPosicaoX(), im.getPosicaoY(), !im.isRotacionado());
    }

    @Transactional
    public String usar(Long id) {
        ItemMaleta im = buscarPorId(id);
        Item item = im.getItem();
        if (!item.isUsavel()) {
            throw new RegraNegocioException("\"" + item.getNome() + "\" nao pode ser usado sozinho."
                    + (item.getCategoria().getTipo() == TipoCategoria.CURA ? " Tente combinar com outra erva." : ""));
        }
        Personagem p = im.getMaleta().getPersonagem();
        int aumento = item.getAumentoVidaMaxima() == null ? 0 : item.getAumentoVidaMaxima();
        if (p.getVida() >= p.getVidaMaxima() && aumento == 0) {
            throw new RegraNegocioException("A vida de " + p.getNome() + " ja esta cheia.");
        }
        int antes = p.getVida();
        p.setVidaMaxima(p.getVidaMaxima() + aumento);
        int novaVida = item.getCura() == Item.CURA_TOTAL ? p.getVidaMaxima() : p.getVida() + item.getCura();
        p.setVida(Math.min(p.getVidaMaxima(), novaVida));
        personagemRepository.save(p);
        consumir(im, 1);

        String msg = p.getNome() + " usou " + item.getNome() + ": vida " + antes + " → " + p.getVida();
        return aumento > 0 ? msg + " (vida maxima +" + aumento + ")." : msg + ".";
    }

    @Transactional
    public String equipar(Long id) {
        ItemMaleta im = buscarPorId(id);
        TipoCategoria tipo = im.getItem().getCategoria().getTipo();
        if (tipo != TipoCategoria.ARMA && tipo != TipoCategoria.GRANADA) {
            throw new RegraNegocioException("So armas e granadas podem ser equipadas.");
        }
        if (im.isEquipada()) {
            im.setEquipada(false);
            itemMaletaRepository.save(im);
            return im.getItem().getNome() + " desequipada.";
        }
        for (ItemMaleta outro : listarPorMaleta(im.getMaleta().getId())) {
            if (outro.isEquipada()) {
                outro.setEquipada(false);
                itemMaletaRepository.save(outro);
            }
        }
        im.setEquipada(true);
        itemMaletaRepository.save(im);
        return im.getItem().getNome() + " equipada.";
    }

    @Transactional
    public String recarregar(Long armaId) {
        ItemMaleta arma = buscarPorId(armaId);
        Item municao = arma.getItem().getMunicao();
        if (!arma.getItem().isArma() || municao == null) {
            throw new RegraNegocioException("\"" + arma.getItem().getNome() + "\" nao usa municao recarregavel.");
        }
        int capacidade = melhoriaService.capacidade(arma);
        int falta = capacidade - arma.getMunicaoCarregada();
        if (falta <= 0) {
            throw new RegraNegocioException(arma.getItem().getNome() + " ja esta totalmente carregada ("
                    + arma.getMunicaoCarregada() + "/" + capacidade + ").");
        }
        int carregado = 0;
        for (ItemMaleta pilha : itemMaletaRepository.findByMaletaIdAndItemIdOrderByQuantidadeAsc(
                arma.getMaleta().getId(), municao.getId())) {
            if (falta == 0) {
                break;
            }
            int qtd = Math.min(falta, pilha.getQuantidade());
            consumir(pilha, qtd);
            falta -= qtd;
            carregado += qtd;
        }
        if (carregado == 0) {
            throw new RegraNegocioException("Sem " + municao.getNome() + " na maleta.");
        }
        arma.setMunicaoCarregada(arma.getMunicaoCarregada() + carregado);
        itemMaletaRepository.save(arma);
        return arma.getItem().getNome() + " recarregada: +" + carregado + " (" + arma.getMunicaoCarregada()
                + "/" + capacidade + ").";
    }

    @Transactional
    public String combinar(Long origemId, Long alvoId) {
        if (Objects.equals(origemId, alvoId)) {
            throw new RegraNegocioException("Escolha outro item para combinar.");
        }
        ItemMaleta origem = buscarPorId(origemId);
        ItemMaleta alvo = buscarPorId(alvoId);
        if (!origem.getMaleta().getId().equals(alvo.getMaleta().getId())) {
            throw new RegraNegocioException("Os itens precisam estar na mesma maleta.");
        }
        Item a = origem.getItem();
        Item b = alvo.getItem();

        if (b.isArma() && mesmo(a, b.getMunicao())) {
            return recarregar(alvo.getId());
        }
        if (a.isArma() && mesmo(b, a.getMunicao())) {
            return recarregar(origem.getId());
        }

        if (a.getId().equals(b.getId()) && a.getEmpilhamentoMaximo() > 1) {
            int cabe = b.getEmpilhamentoMaximo() - alvo.getQuantidade();
            if (cabe <= 0) {
                throw new RegraNegocioException("Esta pilha de " + b.getNome() + " ja esta cheia.");
            }
            int qtd = Math.min(cabe, origem.getQuantidade());
            alvo.setQuantidade(alvo.getQuantidade() + qtd);
            itemMaletaRepository.save(alvo);
            consumir(origem, qtd);
            return "Pilhas de " + b.getNome() + " combinadas (" + alvo.getQuantidade() + ").";
        }

        Combinacao receita = combinacaoService.receita(a.getId(), b.getId())
                .orElseThrow(() -> new RegraNegocioException("Nao e possivel combinar " + a.getNome()
                        + " com " + b.getNome() + "."));
        Item resultado = receita.getResultado();
        Maleta maleta = alvo.getMaleta();
        Integer x = alvo.getPosicaoX();
        Integer y = alvo.getPosicaoY();
        boolean rot = alvo.isRotacionado();

        consumir(origem, 1);
        consumir(alvo, 1);
        itemMaletaRepository.flush();

        ItemMaleta novo;
        if (resultado.isOcupaMaleta() && x != null && cabe(maleta, null, x, y,
                rot ? resultado.getAltura() : resultado.getLargura(),
                rot ? resultado.getLargura() : resultado.getAltura())) {
            novo = itemMaletaRepository.save(new ItemMaleta(maleta, resultado, x, y, 1, rot));
        } else {
            novo = adicionar(maleta, resultado, 1);
        }
        return a.getNome() + " + " + b.getNome() + " = " + novo.getItem().getNome() + ".";
    }

    @Transactional
    public void excluir(Long id) {
        itemMaletaRepository.delete(buscarPorId(id));
    }

    private static boolean mesmo(Item a, Item b) {
        return a != null && b != null && Objects.equals(a.getId(), b.getId());
    }

    void consumir(ItemMaleta im, int qtd) {
        if (im.getQuantidade() > qtd) {
            im.setQuantidade(im.getQuantidade() - qtd);
            itemMaletaRepository.save(im);
        } else {
            itemMaletaRepository.delete(im);
        }
    }

    int[] encontrarEspaco(Maleta maleta, Item item, Long ignorarId) {
        List<ItemMaleta> ocupados = itemMaletaRepository.findByMaletaIdOrderByPosicaoYAscPosicaoXAsc(maleta.getId());
        for (int r = 0; r <= 1; r++) {
            int w = r == 1 ? item.getAltura() : item.getLargura();
            int h = r == 1 ? item.getLargura() : item.getAltura();
            if (r == 1 && w == h) {
                continue;
            }
            for (int y = 0; y + h <= maleta.getLinhas(); y++) {
                for (int x = 0; x + w <= maleta.getColunas(); x++) {
                    int fx = x;
                    int fy = y;
                    boolean livre = ocupados.stream()
                            .filter(o -> !Objects.equals(o.getId(), ignorarId))
                            .noneMatch(o -> o.sobrepoe(fx, fy, w, h));
                    if (livre) {
                        return new int[]{x, y, r};
                    }
                }
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public boolean temEspaco(Maleta maleta, Item item) {
        return !item.isOcupaMaleta() || encontrarEspaco(maleta, item, null) != null;
    }

    private boolean cabe(Maleta maleta, Long idAtual, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > maleta.getColunas() || y + h > maleta.getLinhas()) {
            return false;
        }
        return itemMaletaRepository.findByMaletaIdOrderByPosicaoYAscPosicaoXAsc(maleta.getId()).stream()
                .filter(o -> !Objects.equals(o.getId(), idAtual))
                .noneMatch(o -> o.sobrepoe(x, y, w, h));
    }

    private void validarPosicao(Maleta maleta, Long idAtual, int x, int y, int w, int h, String nomeItem) {
        if (x < 0 || y < 0 || x + w > maleta.getColunas() || y + h > maleta.getLinhas()) {
            throw new RegraNegocioException("\"" + nomeItem + "\" (" + w + "x" + h + ") nao cabe na posicao ("
                    + x + ", " + y + ") de uma maleta " + maleta.getColunas() + "x" + maleta.getLinhas() + ".");
        }
        for (ItemMaleta outro : itemMaletaRepository.findByMaletaIdOrderByPosicaoYAscPosicaoXAsc(maleta.getId())) {
            if (!Objects.equals(outro.getId(), idAtual) && outro.sobrepoe(x, y, w, h)) {
                throw new RegraNegocioException("A posicao (" + x + ", " + y + ") colide com \""
                        + outro.getItem().getNome() + "\".");
            }
        }
    }
}
