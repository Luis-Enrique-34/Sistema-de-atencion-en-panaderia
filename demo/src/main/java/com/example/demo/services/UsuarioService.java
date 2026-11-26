package com.example.demo.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByRolAscNombreAsc();
    }

    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El usuario no existe."));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new NegocioException("El usuario no existe."));
    }

    @Transactional(readOnly = true)
    public boolean correoEnUso(String correo, Long id) {
        String limpio = correo.trim().toLowerCase();
        return id == null
                ? usuarioRepository.existsByCorreo(limpio)
                : usuarioRepository.existsByCorreoAndIdNot(limpio, id);
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setCorreo(usuario.getCorreo().trim().toLowerCase());

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            if (usuario.getId() == null) {
                throw new NegocioException("La contraseña es obligatoria.");
            }
            usuario.setPassword(obtener(usuario.getId()).getPassword());
        } else {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        if (usuario.getTelefono() != null && usuario.getTelefono().isBlank()) {
            usuario.setTelefono(null);
        }
        if (usuario.getDireccion() != null && usuario.getDireccion().isBlank()) {
            usuario.setDireccion(null);
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario registrarCliente(Usuario usuario) {
        usuario.setId(null);
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        return guardar(usuario);
    }

    @Transactional
    public void cambiarEstado(Long id, String correoActual) {
        Usuario usuario = obtener(id);
        if (usuario.getCorreo().equals(correoActual)) {
            throw new NegocioException("No puedes desactivar tu propia cuenta.");
        }
        usuario.setActivo(!usuario.isActivo());
    }

    @Transactional
    public void eliminar(Long id, String correoActual) {
        Usuario usuario = obtener(id);
        if (usuario.getCorreo().equals(correoActual)) {
            throw new NegocioException("No puedes eliminar tu propia cuenta.");
        }
        if (pedidoRepository.existsByClienteId(id)) {
            throw new NegocioException(usuario.getNombre() + " tiene pedidos registrados. Desactívalo en lugar de eliminarlo.");
        }
        usuarioRepository.delete(usuario);
    }
}
