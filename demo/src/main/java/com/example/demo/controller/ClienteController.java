package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Cliente;
import com.example.demo.services.ClienteService;
import com.example.demo.services.NegocioException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        model.addAttribute("clientes", service.listar(buscar));
        model.addAttribute("buscar", buscar);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("cliente", service.obtener(id));
            return "clientes/formulario";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result,
            RedirectAttributes ra) {
        if (!result.hasFieldErrors("dni") && service.dniEnUso(cliente.getDni(), cliente.getId())) {
            result.rejectValue("dni", "duplicado", "Ya existe un cliente con ese DNI");
        }
        if (result.hasErrors()) {
            return "clientes/formulario";
        }
        boolean esNuevo = cliente.getId() == null;
        service.guardar(cliente);
        ra.addFlashAttribute("mensaje", esNuevo ? "Cliente registrado." : "Cliente actualizado.");
        return "redirect:/clientes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.eliminar(id);
            ra.addFlashAttribute("mensaje", "Cliente eliminado.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }
}
