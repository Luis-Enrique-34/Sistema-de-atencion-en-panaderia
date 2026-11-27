package com.example.demo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Usuario;
import com.example.demo.services.PedidoService;
import com.example.demo.services.ProductoService;
import com.example.demo.services.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class InicioController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final ProductoService productoService;

    public InicioController(UsuarioService usuarioService, PedidoService pedidoService,
            ProductoService productoService) {
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.productoService = productoService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result,
            @RequestParam String confirmar, RedirectAttributes redirect) {

        if (!result.hasFieldErrors("correo") && usuarioService.correoEnUso(usuario.getCorreo(), null)) {
            result.rejectValue("correo", "duplicado", "Ese correo ya está registrado");
        }
        if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
            result.rejectValue("password", "corta", "La contraseña debe tener al menos 6 caracteres");
        } else if (!usuario.getPassword().equals(confirmar)) {
            result.rejectValue("password", "diferente", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) {
            return "registro";
        }

        usuarioService.registrarCliente(usuario);
        redirect.addFlashAttribute("mensaje", "Cuenta creada. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/menu";
    }

    @GetMapping("/menu")
    public String menu(Principal principal, Model model) {
        Usuario usuario = usuarioService.buscarPorCorreo(principal.getName());

        if (usuario.isCliente()) {
            model.addAttribute("totalPedidos", pedidoService.contarDeCliente(usuario.getCorreo()));
        } else {
            model.addAttribute("pendientes", pedidoService.contar(EstadoPedido.PENDIENTE));
            model.addAttribute("enPreparacion", pedidoService.contar(EstadoPedido.EN_PREPARACION));
            model.addAttribute("listos", pedidoService.contar(EstadoPedido.LISTO));
            model.addAttribute("stockBajo", productoService.listarStockBajo());
        }
        return "inicio";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
