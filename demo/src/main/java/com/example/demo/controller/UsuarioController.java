package com.example.demo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.services.NegocioException;
import com.example.demo.services.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("roles")
    public Rol[] roles() {
        return Rol.values();
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            Usuario usuario = usuarioService.obtener(id);
            usuario.setPassword(null);
            model.addAttribute("usuario", usuario);
            return "usuarios/formulario";
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result,
            Principal principal, RedirectAttributes redirect) {

        if (!result.hasFieldErrors("correo") && usuarioService.correoEnUso(usuario.getCorreo(), usuario.getId())) {
            result.rejectValue("correo", "duplicado", "Ese correo ya está registrado");
        }

        String password = usuario.getPassword();
        boolean sinPassword = password == null || password.isBlank();
        if (usuario.getId() == null && sinPassword) {
            result.rejectValue("password", "requerida", "La contraseña es obligatoria");
        } else if (!sinPassword && password.length() < 6) {
            result.rejectValue("password", "corta", "Mínimo 6 caracteres");
        }

        if (usuario.getId() != null && !result.hasFieldErrors("correo")) {
            Usuario original = usuarioService.obtener(usuario.getId());
            boolean esMiCuenta = original.getCorreo().equals(principal.getName());
            boolean cambiaAlgoCritico = !usuario.isAdmin() || !usuario.isActivo()
                    || !usuario.getCorreo().trim().equalsIgnoreCase(original.getCorreo());
            if (esMiCuenta && cambiaAlgoCritico) {
                result.reject("cuentaPropia", "En tu propia cuenta no puedes cambiar el rol, el correo ni desactivarte.");
            }
        }

        if (result.hasErrors()) {
            return "usuarios/formulario";
        }

        boolean esNuevo = usuario.getId() == null;
        usuarioService.guardar(usuario);
        redirect.addFlashAttribute("mensaje", esNuevo ? "Usuario registrado." : "Usuario actualizado.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {
        try {
            usuarioService.cambiarEstado(id, principal.getName());
            redirect.addFlashAttribute("mensaje", "Estado del usuario actualizado.");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {
        try {
            usuarioService.eliminar(id, principal.getName());
            redirect.addFlashAttribute("mensaje", "Usuario eliminado.");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
