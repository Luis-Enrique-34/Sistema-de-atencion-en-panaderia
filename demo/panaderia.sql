CREATE DATABASE IF NOT EXISTS panaderia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE panaderia_db;

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

INSERT IGNORE INTO usuarios (id, nombre, correo, password, rol, activo) VALUES
 (1, 'Administrador',  'admin@panaderia.pe',    '$2a$10$3xTY43ITTXVZtGQvX7VlT.LJNxdGQGnV49.QTGQ/TanaEEeoHwqZu', 'ADMIN',    1),
 (2, 'Pedro Panadero', 'panadero@panaderia.pe', '$2a$10$HyYgYKflEYCjWnzn8BTeP.Glo/BZubxHrRolLEblEZFZx/gQlbiHq', 'PANADERO', 1),
 (3, 'María Cliente',  'cliente@panaderia.pe',  '$2a$10$zNRcqF9VQc34AUAa5fQyAOerHWgc77kCBzoWnY5e02Qo1WEhYcUNW', 'CLIENTE',  1);

INSERT IGNORE INTO categorias (id, nombre, descripcion) VALUES
 (1, 'Panes salados', 'Panes del día para el desayuno'),
 (2, 'Panes dulces',  'Panes con azúcar, yema o leche'),
 (3, 'Integrales',    'Panes con harina integral y granos'),
 (4, 'Especiales',    'Panes por unidad o paquete');

INSERT IGNORE INTO productos (id, nombre, categoria_id, precio, stock, descripcion) VALUES
 (1,  'Pan francés',    1, 0.30, 300, 'Crocante, horneado cada mañana'),
 (2,  'Pan ciabatta',   1, 0.50, 150, 'Miga suave con aceite de oliva'),
 (3,  'Baguette',       1, 2.50,  40, 'Pan largo de corteza dorada'),
 (4,  'Pan de yema',    2, 0.40, 200, 'Suave y ligeramente dulce'),
 (5,  'Chancay',        2, 0.60, 120, 'Pan dulce tradicional'),
 (6,  'Pan de leche',   2, 0.50, 100, NULL),
 (7,  'Pan integral',   3, 0.50, 120, 'Con harina de trigo integral'),
 (8,  'Pan de centeno', 3, 0.80,  60, NULL),
 (9,  'Pan de molde',   4, 7.50,  25, 'Bolsa de 500 g'),
 (10, 'Pan de ajo',     4, 1.20,  15, 'Con mantequilla y perejil');
