package com.example.demo.services;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Listar usuarios ordenados por ID
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by("id"));
    }

    // Buscar usuario por ID
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Verifica si el correo ya lo usa otro usuario
    @Transactional(readOnly = true)
    public boolean correoEnUso(String correo, Long id) {
        return (id == null)
                ? usuarioRepository.existsByCorreo(correo)
                : usuarioRepository.existsByCorreoAndIdNot(correo, id);
    }

    // Registrar o actualizar usuario
    @Transactional
    public Usuario guardar(Usuario usuario) {
        // Al editar, si la contraseña viene vacía se conserva la anterior
        if (usuario.getId() != null && (usuario.getPassword() == null || usuario.getPassword().isBlank())) {
            usuarioRepository.findById(usuario.getId())
                    .ifPresent(actual -> usuario.setPassword(actual.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    // Eliminar usuario (devuelve false si no existía)
    @Transactional
    public boolean eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }
}
