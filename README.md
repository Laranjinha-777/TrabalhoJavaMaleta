# Maleta – Sistema de Inventário

Sistema web acadêmico feito com **Spring Boot (Spring MVC + Thymeleaf + Spring Data JPA)**,
com o inventário em grade e o mercador baseados em *Resident Evil 4* (2005): mover e girar
itens na maleta, combinar ervas, usar, equipar, recarregar, comprar e vender armas,
melhorar armas e trocar a maleta por uma maior.

A documentação da 1ª etapa (tema, diagrama de classes, MER e protótipos) está em
[`docs/ETAPA1.md`](docs/ETAPA1.md).

## Como rodar

Pré-requisito: **JDK 17 ou superior** (com `JAVA_HOME` configurado). **Não precisa instalar o Maven**: o
projeto traz o Maven Wrapper (`mvnw`), que baixa o Maven sozinho na primeira vez.

Windows (PowerShell ou cmd):

```bash
.\mvnw.cmd spring-boot:run
```

Linux / macOS / Git Bash:

```bash
./mvnw spring-boot:run
```

Abra http://localhost:8090. Na primeira execução o banco é populado com armas (e suas tabelas
de melhoria), munições, ervas e combinações, granadas, tesouros, tamanhos de maleta e dois
personagens com maletas já montadas.

- Se a porta 8090 estiver ocupada: `$env:PORT=8091` (PowerShell) ou `set PORT=8091` (cmd) antes de rodar.
- Console do banco H2: http://localhost:8090/h2-console (JDBC URL `jdbc:h2:file:./data/inventario`, usuário `sa`, sem senha).
- Para zerar os dados, pare o sistema e apague a pasta `data/`.

### Usando MySQL

1. `CREATE DATABASE inventario_maleta;`
2. Ajuste usuário/senha em `src/main/resources/application-mysql.properties`.
3. `.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql`

## Requisitos da atividade × onde estão

| Requisito | Onde |
|---|---|
| Banco relacional | H2 em arquivo (`./data`) por padrão, ou MySQL (perfil `mysql`) |
| 4+ tabelas | 9 tabelas: `personagem`, `tamanho_maleta`, `maleta`, `categoria`, `item`, `melhoria`, `combinacao`, `item_maleta`, `transacao` |
| CRUDs | Categorias, Itens, Melhorias, Combinações, Tamanhos de maleta, Personagens, Maletas, Itens nas maletas (+ histórico com listagem/exclusão) |
| Model: Entity, Repository, Service | `model/entity`, `model/repository`, `model/service` |
| Control: Controllers | `controller/` (só usam Services) |
| View: HTML/CSS/JS | `resources/templates`, `resources/static/css`, `resources/static/js` |

## Como usar o inventário

Na tela da maleta (`Maletas → abrir`):

- **Clique** em um item para ver os detalhes e o menu de ações.
- **Arraste** um item para movê-lo; durante o arraste, aperte **R** (ou o botão direito) para girar. Verde = cabe, vermelho = não cabe.
- **Combinar**: clique em "Combinar" e depois no outro item (ervas, munição + arma, pilhas iguais).
- **Usar** (cura), **Equipar**, **Recarregar**, **Girar**, **Descartar**.
- **Pegar item** simula pegar algo do cenário: completa pilhas e procura espaço livre.
- **Mercador**: Comprar (armas, granadas, spray, maletas maiores), Vender, Melhorar armas.

## Estrutura

```
src/main/java/com/faculdade/inventario
├── config/          DadosIniciais (dados de exemplo), WebConfig (conversão id → entidade)
├── controller/      Home, Maleta, ItemMaleta, Mercador, Item, Melhoria, Combinacao,
│                    Categoria, TamanhoMaleta, Personagem, Transacao, Erro
└── model/
    ├── entity/      entidades JPA + enums (TipoCategoria, AtributoArma, TipoTransacao)
    ├── repository/  interfaces Spring Data JPA
    └── service/     regras do inventário (ItemMaletaService), mercador (MercadorService)...
```
