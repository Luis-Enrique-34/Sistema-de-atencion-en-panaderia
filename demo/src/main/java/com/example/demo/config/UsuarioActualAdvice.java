package com.example.demo.config;

import java.security.Principal;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

@ControllerAdvice
public class UsuarioActualAdvice {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActualAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(Principal principal) {
        if (principal == null) {
            return null;
        }
        return usuarioRepository.findByCorreo(principal.getName()).orElse(null);
    }
}
