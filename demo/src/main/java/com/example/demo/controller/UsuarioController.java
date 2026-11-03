package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Usuario;
import com.example.demo.services.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // LISTAR
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("listaUsuarios", usuarioService.listar());
        return "lista";
    }

    // FORMULARIO NUEVO
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "formulario";
    }

    // EDITAR: CARGAR DATOS EXISTENTES
    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "El usuario con ID " + id + " no existe.");
            return "redirect:/usuarios";
        }
        usuario.setPassword(null); // no se envía la contraseña al navegador
        model.addAttribute("usuario", usuario);
        return "formulario";
    }

    // GUARDAR O ACTUALIZAR
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        boolean esNuevo = (usuario.getId() == null);

        // Contraseña obligatoria solo al registrar
        if (esNuevo && (usuario.getPassword() == null || usuario.getPassword().isBlank())) {
            result.rejectValue("password", "requerido", "La contraseña es obligatoria");
        }

        // Correo único
        if (!result.hasFieldErrors("correo") && usuarioService.correoEnUso(usuario.getCorreo(), usuario.getId())) {
            result.rejectValue("correo", "duplicado", "Ese correo ya está registrado");
        }

        if (result.hasErrors()) {
            return "formulario"; // vuelve al formulario mostrando los errores
        }

        usuarioService.guardar(usuario);
        redirectAttributes.addFlashAttribute("mensaje",
                esNuevo ? "Usuario registrado correctamente." : "Usuario actualizado correctamente.");
        return "redirect:/usuarios";
    }

    // ELIMINAR (por POST, para que no se borre con solo abrir un enlace)
    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (usuarioService.eliminar(id)) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "El usuario con ID " + id + " no existe.");
        }
        return "redirect:/usuarios";
    }
}
