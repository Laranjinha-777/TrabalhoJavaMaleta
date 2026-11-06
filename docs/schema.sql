-- Script equivalente ao que o Hibernate gera (ddl-auto=update).
-- Compatível com MySQL / H2.

CREATE TABLE personagem (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(80) NOT NULL,
    ocupacao     VARCHAR(80),
    pesetas      INT NOT NULL,
    vida         INT NOT NULL,
    vida_maxima  INT NOT NULL
);

CREATE TABLE tamanho_maleta (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome     VARCHAR(40) NOT NULL UNIQUE,
    colunas  INT NOT NULL,
    linhas   INT NOT NULL,
    preco    INT NOT NULL,
    ordem    INT NOT NULL
);

CREATE TABLE categoria (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    tipo      VARCHAR(20) NOT NULL,   -- ARMA, MUNICAO, CURA, GRANADA, TESOURO, CHAVE
    cor       VARCHAR(7) NOT NULL
);

CREATE TABLE item (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome                 VARCHAR(80) NOT NULL,
    descricao            VARCHAR(500),
    largura              INT NOT NULL,
    altura               INT NOT NULL,
    preco                INT NOT NULL,
    vendido_mercador     BOOLEAN NOT NULL,
    valor_venda          INT NOT NULL,
    empilhamento_maximo  INT NOT NULL,
    cura                 INT,
    aumento_vida_maxima  INT,
    imagem               VARCHAR(255),         -- /img/itens/x.svg ou /uploads/x.png
    categoria_id         BIGINT NOT NULL,
    municao_id           BIGINT,
    CONSTRAINT fk_item_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_item_municao   FOREIGN KEY (municao_id)   REFERENCES item (id)
);

CREATE TABLE melhoria (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    arma_id           BIGINT NOT NULL,
    atributo          VARCHAR(20) NOT NULL,  -- PODER, CADENCIA, RECARGA, CAPACIDADE, EXCLUSIVA
    nivel             INT NOT NULL,
    valor             DOUBLE,
    custo             INT NOT NULL,
    descricao         VARCHAR(200),
    atributo_afetado  VARCHAR(20),
    CONSTRAINT fk_melhoria_arma FOREIGN KEY (arma_id) REFERENCES item (id),
    CONSTRAINT uk_melhoria UNIQUE (arma_id, atributo, nivel)
);

CREATE TABLE combinacao (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_a_id     BIGINT NOT NULL,
    item_b_id     BIGINT NOT NULL,
    resultado_id  BIGINT NOT NULL,
    CONSTRAINT fk_comb_a FOREIGN KEY (item_a_id)    REFERENCES item (id),
    CONSTRAINT fk_comb_b FOREIGN KEY (item_b_id)    REFERENCES item (id),
    CONSTRAINT fk_comb_r FOREIGN KEY (resultado_id) REFERENCES item (id)
);

CREATE TABLE maleta (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(60) NOT NULL,
    tamanho_id     BIGINT NOT NULL,
    personagem_id  BIGINT NOT NULL,
    CONSTRAINT fk_maleta_tamanho    FOREIGN KEY (tamanho_id)    REFERENCES tamanho_maleta (id),
    CONSTRAINT fk_maleta_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id)
);

CREATE TABLE item_maleta (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    maleta_id          BIGINT NOT NULL,
    item_id            BIGINT NOT NULL,
    posicao_x          INT,             -- nulo para tesouros / itens-chave
    posicao_y          INT,
    quantidade         INT NOT NULL,
    rotacionado        BOOLEAN NOT NULL,
    equipada           BOOLEAN NOT NULL,
    municao_carregada  INT NOT NULL,
    nivel_poder        INT NOT NULL,
    nivel_cadencia     INT NOT NULL,
    nivel_recarga      INT NOT NULL,
    nivel_capacidade   INT NOT NULL,
    exclusiva          BOOLEAN NOT NULL,
    investido          INT NOT NULL,
    CONSTRAINT fk_im_maleta FOREIGN KEY (maleta_id) REFERENCES maleta (id),
    CONSTRAINT fk_im_item   FOREIGN KEY (item_id)   REFERENCES item (id)
);

CREATE TABLE transacao (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    personagem_id  BIGINT NOT NULL,
    tipo           VARCHAR(20) NOT NULL,  -- COMPRA, VENDA, MELHORIA, MALETA
    descricao      VARCHAR(200) NOT NULL,
    valor          INT NOT NULL,
    data_hora      TIMESTAMP NOT NULL,
    CONSTRAINT fk_transacao_personagem FOREIGN KEY (personagem_id) REFERENCES personagem (id)
);
