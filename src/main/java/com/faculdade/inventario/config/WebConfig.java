package com.faculdade.inventario.config;

import com.faculdade.inventario.model.entity.Categoria;
import com.faculdade.inventario.model.entity.Item;
import com.faculdade.inventario.model.entity.Maleta;
import com.faculdade.inventario.model.entity.Personagem;
import com.faculdade.inventario.model.entity.TamanhoMaleta;
import com.faculdade.inventario.model.service.CategoriaService;
import com.faculdade.inventario.model.service.ImagemService;
import com.faculdade.inventario.model.service.ItemService;
import com.faculdade.inventario.model.service.MaletaService;
import com.faculdade.inventario.model.service.PersonagemService;
import com.faculdade.inventario.model.service.TamanhoMaletaService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Locale;
import java.util.function.Function;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CategoriaService categoriaService;
    private final ItemService itemService;
    private final PersonagemService personagemService;
    private final MaletaService maletaService;
    private final TamanhoMaletaService tamanhoService;
    private final ImagemService imagemService;

    public WebConfig(CategoriaService categoriaService, ItemService itemService,
                     PersonagemService personagemService, MaletaService maletaService,
                     TamanhoMaletaService tamanhoService, ImagemService imagemService) {
        this.categoriaService = categoriaService;
        this.itemService = itemService;
        this.personagemService = personagemService;
        this.maletaService = maletaService;
        this.tamanhoService = tamanhoService;
        this.imagemService = imagemService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(imagemService.getPasta().toUri().toString());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(Categoria.class, formatter(categoriaService::buscarPorId, Categoria::getId));
        registry.addFormatterForFieldType(Item.class, formatter(itemService::buscarPorId, Item::getId));
        registry.addFormatterForFieldType(Personagem.class, formatter(personagemService::buscarPorId, Personagem::getId));
        registry.addFormatterForFieldType(Maleta.class, formatter(maletaService::buscarPorId, Maleta::getId));
        registry.addFormatterForFieldType(TamanhoMaleta.class, formatter(tamanhoService::buscarPorId, TamanhoMaleta::getId));
    }

    private static <T> Formatter<T> formatter(Function<Long, T> buscar, Function<T, Long> id) {
        return new Formatter<>() {
            @Override
            public T parse(String texto, Locale locale) {
                return (texto == null || texto.isBlank()) ? null : buscar.apply(Long.valueOf(texto));
            }

            @Override
            public String print(T objeto, Locale locale) {
                Long valor = objeto == null ? null : id.apply(objeto);
                return valor == null ? "" : valor.toString();
            }
        };
    }
}
