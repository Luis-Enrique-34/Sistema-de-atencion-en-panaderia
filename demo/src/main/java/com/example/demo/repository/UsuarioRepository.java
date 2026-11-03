package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Para evitar correos duplicados al registrar
    boolean existsByCorreo(String correo);

    // Para evitar correos duplicados al editar (excluye al propio usuario)
    boolean existsByCorreoAndIdNot(String correo, Long id);
}
