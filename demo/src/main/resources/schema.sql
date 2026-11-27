CREATE TABLE IF NOT EXISTS usuarios (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(100) NOT NULL,
  correo     VARCHAR(120) NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  telefono   VARCHAR(9)   NULL,
  direccion  VARCHAR(150) NULL,
  rol        VARCHAR(20)  NOT NULL,
  activo     BIT(1)       NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categorias (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(60)  NOT NULL UNIQUE,
  descripcion  VARCHAR(150) NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS productos (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(80)  NOT NULL UNIQUE,
  categoria_id  BIGINT       NOT NULL,
  precio        DECIMAL(8,2) NOT NULL,
  stock         INT          NOT NULL,
  descripcion   VARCHAR(255) NULL,
  CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pedidos (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  cliente_id     BIGINT        NOT NULL,
  fecha          DATETIME(6)   NOT NULL,
  fecha_entrega  DATE          NOT NULL,
  estado         VARCHAR(20)   NOT NULL,
  total          DECIMAL(10,2) NOT NULL,
  observaciones  VARCHAR(255)  NULL,
  CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_pedidos (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  pedido_id        BIGINT        NOT NULL,
  producto_id      BIGINT        NOT NULL,
  cantidad         INT           NOT NULL,
  precio_unitario  DECIMAL(10,2) NOT NULL,
  subtotal         DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_detalle_pedido   FOREIGN KEY (pedido_id)   REFERENCES pedidos(id),
  CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB;
