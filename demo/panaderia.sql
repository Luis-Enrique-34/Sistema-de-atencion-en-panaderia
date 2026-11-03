-- =====================================================================
--  Panadería El Buen Pan - Base de datos (MySQL / MariaDB de XAMPP)
--  Uso opcional: phpMyAdmin > Importar > este archivo.
--  La aplicación Spring Boot también crea estas tablas sola al arrancar.
-- =====================================================================
CREATE DATABASE IF NOT EXISTS panaderia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE panaderia;

-- 1) PRODUCTOS
CREATE TABLE IF NOT EXISTS productos (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(80)   NOT NULL UNIQUE,
  categoria    VARCHAR(40)   NOT NULL,
  precio       DECIMAL(8,2)  NOT NULL,
  stock        INT           NOT NULL,
  descripcion  VARCHAR(255)  NULL
) ENGINE=InnoDB;

-- 2) CLIENTES
CREATE TABLE IF NOT EXISTS clientes (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(100) NOT NULL,
  dni        CHAR(8)      NOT NULL UNIQUE,
  telefono   VARCHAR(9)   NULL,
  direccion  VARCHAR(150) NULL
) ENGINE=InnoDB;

-- 3) PEDIDOS (relaciona clientes y productos)
CREATE TABLE IF NOT EXISTS pedidos (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  cliente_id       BIGINT        NOT NULL,
  producto_id      BIGINT        NOT NULL,
  cantidad         INT           NOT NULL,
  precio_unitario  DECIMAL(10,2) NOT NULL,
  total            DECIMAL(10,2) NOT NULL,
  fecha            DATETIME(6)   NOT NULL,
  estado           VARCHAR(20)   NOT NULL,   -- PENDIENTE / ENTREGADO / ANULADO
  observacion      VARCHAR(255)  NULL,
  CONSTRAINT fk_pedido_cliente  FOREIGN KEY (cliente_id)  REFERENCES clientes(id),
  CONSTRAINT fk_pedido_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB;

-- 4) USUARIOS (acceso al sistema)
CREATE TABLE IF NOT EXISTS usuarios (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre    VARCHAR(100) NOT NULL,
  correo    VARCHAR(150) NOT NULL UNIQUE,
  password  VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- Datos de ejemplo (INSERT IGNORE: no duplica si ya existen)
INSERT IGNORE INTO productos (id, nombre, categoria, precio, stock, descripcion) VALUES
 (1, 'Pan francés',        'Panes',      0.30, 200, 'Crocante, horneado cada mañana'),
 (2, 'Pan de yema',        'Panes',      0.40, 120, NULL),
 (3, 'Torta de chocolate', 'Pastelería', 45.00,  5, 'Entera, 12 porciones'),
 (4, 'Empanada de carne',  'Bocaditos',  4.50,  30, NULL),
 (5, 'Café pasado',        'Bebidas',    3.50,  50, NULL);

INSERT IGNORE INTO clientes (id, nombre, dni, telefono, direccion) VALUES
 (1, 'María Torres', '45678912', '987654321', 'Av. Arequipa 123, Lima'),
 (2, 'Jorge Quispe', '70123456', '912345678', NULL);

INSERT IGNORE INTO usuarios (id, nombre, correo, password) VALUES
 (1, 'Administrador', 'admin@elbuenpan.pe', 'admin123');

-- Consultas útiles para revisar en phpMyAdmin
-- SHOW TABLES;
-- SELECT p.id, c.nombre AS cliente, pr.nombre AS producto, p.cantidad, p.total, p.estado
--   FROM pedidos p JOIN clientes c ON c.id = p.cliente_id JOIN productos pr ON pr.id = p.producto_id;
