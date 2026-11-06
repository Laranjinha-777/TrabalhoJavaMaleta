package com.faculdade.inventario.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImagemService {

    private static final Map<String, String> TIPOS = Map.of(
            "image/png", ".png",
            "image/jpeg", ".jpg",
            "image/webp", ".webp",
            "image/gif", ".gif");

    private final Path pasta;

    public ImagemService(@Value("${app.uploads:./uploads}") String pasta) {
        this.pasta = Path.of(pasta).toAbsolutePath().normalize();
    }

    public Path getPasta() {
        return pasta;
    }

    public String salvar(MultipartFile arquivo) {
        String extensao = TIPOS.get(arquivo.getContentType());
        if (extensao == null) {
            throw new RegraNegocioException("Formato de imagem nao suportado. Use PNG, JPG, WEBP ou GIF.");
        }
        if (arquivo.getSize() > 2 * 1024 * 1024) {
            throw new RegraNegocioException("A imagem deve ter no maximo 2 MB.");
        }
        try (InputStream in = arquivo.getInputStream()) {
            byte[] cabecalho = in.readNBytes(12);
            if (!assinaturaValida(cabecalho, extensao)) {
                throw new RegraNegocioException("O arquivo enviado nao parece ser uma imagem valida.");
            }
            Files.createDirectories(pasta);
            String nome = UUID.randomUUID() + extensao;
            try (InputStream completo = arquivo.getInputStream()) {
                Files.copy(completo, pasta.resolve(nome), StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + nome;
        } catch (IOException e) {
            throw new RegraNegocioException("Nao foi possivel salvar a imagem: " + e.getMessage());
        }
    }

    public List<String> listarIcones() {
        try {
            Resource[] r = new PathMatchingResourcePatternResolver().getResources("classpath:/static/img/itens/*.svg");
            return Arrays.stream(r).map(Resource::getFilename).filter(Objects::nonNull).sorted()
                    .map(n -> "/img/itens/" + n).toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private static boolean assinaturaValida(byte[] b, String ext) {
        if (b.length < 4) {
            return false;
        }
        return switch (ext) {
            case ".png" -> (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
            case ".jpg" -> (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8;
            case ".gif" -> b[0] == 'G' && b[1] == 'I' && b[2] == 'F';
            case ".webp" -> b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[8] == 'W' && b[9] == 'E';
            default -> false;
        };
    }
}
