package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.Categoria;
import com.example.demo.model.Producto;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.UsuarioRepository;

@Configuration
public class DatosIniciales {

    @Bean
    public CommandLineRunner cargarDatos(UsuarioRepository usuarios, CategoriaRepository categorias,
            ProductoRepository productos, PasswordEncoder encoder) {
        return args -> {
            if (usuarios.count() == 0) {
                usuarios.save(new Usuario("Administrador", "admin@panaderia.pe", encoder.encode("admin123"), Rol.ADMIN));
                usuarios.save(new Usuario("Pedro Panadero", "panadero@panaderia.pe", encoder.encode("panadero123"), Rol.PANADERO));
                usuarios.save(new Usuario("María Cliente", "cliente@panaderia.pe", encoder.encode("cliente123"), Rol.CLIENTE));
            }

            if (categorias.count() == 0) {
                Categoria salados = categorias.save(new Categoria("Panes salados", "Panes del día para el desayuno"));
                Categoria dulces = categorias.save(new Categoria("Panes dulces", "Panes con azúcar, yema o leche"));
                Categoria integrales = categorias.save(new Categoria("Integrales", "Panes con harina integral y granos"));
                Categoria especiales = categorias.save(new Categoria("Especiales", "Panes por unidad o paquete"));

                productos.save(new Producto("Pan francés", salados, "0.30", 300, "Crocante, horneado cada mañana"));
                productos.save(new Producto("Pan ciabatta", salados, "0.50", 150, "Miga suave con aceite de oliva"));
                productos.save(new Producto("Baguette", salados, "2.50", 40, "Pan largo de corteza dorada"));
                productos.save(new Producto("Pan de yema", dulces, "0.40", 200, "Suave y ligeramente dulce"));
                productos.save(new Producto("Chancay", dulces, "0.60", 120, "Pan dulce tradicional"));
                productos.save(new Producto("Pan de leche", dulces, "0.50", 100, null));
                productos.save(new Producto("Pan integral", integrales, "0.50", 120, "Con harina de trigo integral"));
                productos.save(new Producto("Pan de centeno", integrales, "0.80", 60, null));
                productos.save(new Producto("Pan de molde", especiales, "7.50", 25, "Bolsa de 500 g"));
                productos.save(new Producto("Pan de ajo", especiales, "1.20", 15, "Con mantequilla y perejil"));
            }
        };
    }
}
