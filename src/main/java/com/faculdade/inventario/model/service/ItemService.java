package com.faculdade.inventario.model.service;

import com.faculdade.inventario.model.entity.Item;
import com.faculdade.inventario.model.entity.TipoCategoria;
import com.faculdade.inventario.model.repository.CombinacaoRepository;
import com.faculdade.inventario.model.repository.ItemMaletaRepository;
import com.faculdade.inventario.model.repository.ItemRepository;
import com.faculdade.inventario.model.repository.MelhoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMaletaRepository itemMaletaRepository;
    private final MelhoriaRepository melhoriaRepository;
    private final CombinacaoRepository combinacaoRepository;

    public ItemService(ItemRepository itemRepository, ItemMaletaRepository itemMaletaRepository,
                       MelhoriaRepository melhoriaRepository, CombinacaoRepository combinacaoRepository) {
        this.itemRepository = itemRepository;
        this.itemMaletaRepository = itemMaletaRepository;
        this.melhoriaRepository = melhoriaRepository;
        this.combinacaoRepository = combinacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Item> listarTodos() {
        return itemRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<Item> listarPorTipo(TipoCategoria tipo) {
        return itemRepository.findByCategoriaTipoOrderByNomeAsc(tipo);
    }

    @Transactional(readOnly = true)
    public List<Item> listarAVenda() {
        return itemRepository.findByVendidoMercadorTrueOrderByCategoriaNomeAscPrecoAsc();
    }

    @Transactional(readOnly = true)
    public List<Item> pesquisar(String nome, Long categoriaId) {
        List<Item> itens = (nome == null || nome.isBlank())
                ? itemRepository.findAllByOrderByNomeAsc()
                : itemRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim());
        if (categoriaId != null) {
            itens = itens.stream().filter(i -> i.getCategoria().getId().equals(categoriaId)).toList();
        }
        return itens;
    }

    @Transactional(readOnly = true)
    public Item buscarPorId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Item nao encontrado: " + id));
    }

    @Transactional
    public Item salvar(Item item) {
        if (!item.isArma()) {
            item.setMunicao(null);
        } else if (item.getMunicao() != null && item.getMunicao().getCategoria().getTipo() != TipoCategoria.MUNICAO) {
            throw new RegraNegocioException("A municao da arma precisa ser um item da categoria do tipo Municao.");
        }
        if (item.getId() != null && itemMaletaRepository.countByItemId(item.getId()) > 0) {
            Item original = buscarPorId(item.getId());
            boolean mudouTamanho = !original.getLargura().equals(item.getLargura())
                    || !original.getAltura().equals(item.getAltura());
            if (mudouTamanho) {
                throw new RegraNegocioException("Nao e possivel alterar o tamanho de um item que ja esta "
                        + "em alguma maleta. Remova-o das maletas primeiro.");
            }
        }
        return itemRepository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        Item item = buscarPorId(id);
        long usos = itemMaletaRepository.countByItemId(id);
        if (usos > 0) {
            throw new RegraNegocioException("\"" + item.getNome() + "\" esta em " + usos
                    + " maleta(s) e nao pode ser excluido.");
        }
        if (combinacaoRepository.contarUsoDoItem(id) > 0) {
            throw new RegraNegocioException("\"" + item.getNome() + "\" e usado em combinacoes. Exclua as combinacoes antes.");
        }
        if (itemRepository.countByMunicaoId(id) > 0) {
            throw new RegraNegocioException("\"" + item.getNome() + "\" e a municao de alguma arma.");
        }
        melhoriaRepository.deleteByArmaId(id);
        itemRepository.delete(item);
    }

    @Transactional
    public void definirImagensPadrao(java.util.Map<String, String> imagemPorNome) {
        for (Item item : itemRepository.findAll()) {
            String img = imagemPorNome.get(item.getNome());
            if (item.getImagem() == null && img != null) {
                item.setImagem(img);
            }
        }
    }

    @Transactional(readOnly = true)
    public long contar() {
        return itemRepository.count();
    }
}
