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
