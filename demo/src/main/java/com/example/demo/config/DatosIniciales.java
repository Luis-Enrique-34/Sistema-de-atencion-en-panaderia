package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.model.Cliente;
import com.example.demo.model.Producto;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ProductoRepository;

// Carga datos de ejemplo solo si las tablas están vacías
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarDatos(ProductoRepository productos, ClienteRepository clientes) {
        return args -> {
            if (productos.count() == 0) {
                productos.save(new Producto("Pan francés", "Panes", "0.30", 200, "Crocante, horneado cada mañana"));
                productos.save(new Producto("Pan de yema", "Panes", "0.40", 120, null));
                productos.save(new Producto("Torta de chocolate", "Pastelería", "45.00", 5, "Entera, 12 porciones"));
                productos.save(new Producto("Empanada de carne", "Bocaditos", "4.50", 30, null));
                productos.save(new Producto("Café pasado", "Bebidas", "3.50", 50, null));
            }
            if (clientes.count() == 0) {
                clientes.save(new Cliente("María Torres", "45678912", "987654321", "Av. Arequipa 123, Lima"));
                clientes.save(new Cliente("Jorge Quispe", "70123456", "912345678", null));
            }
        };
    }
}
