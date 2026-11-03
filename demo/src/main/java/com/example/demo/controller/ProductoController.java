package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Producto;
import com.example.demo.services.NegocioException;
import com.example.demo.services.ProductoService;

import jakarta.validation.Valid;

@Controller
public class ProductoController {

    public static final List<String> CATEGORIAS = List.of("Panes", "Pastelería", "Bocaditos", "Bebidas");

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/productos";
    }

    @GetMapping("/productos")
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        model.addAttribute("productos", service.listar(buscar));
        model.addAttribute("buscar", buscar);
        return "productos/lista";
    }

    @GetMapping("/productos/nuevo")
    public String nuevo(Model model) {
        return formulario(new Producto(), model);
    }

    @GetMapping("/productos/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            return formulario(service.obtener(id), model);
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos";
        }
    }

    @PostMapping("/productos/guardar")
    public String guardar(@Valid @ModelAttribute("producto") Producto producto, BindingResult result,
                          Model model, RedirectAttributes ra) {
        if (!result.hasFieldErrors("nombre") && service.nombreEnUso(producto.getNombre(), producto.getId())) {
            result.rejectValue("nombre", "duplicado", "Ya existe un producto con ese nombre");
        }
        if (result.hasErrors()) {
            return formulario(producto, model);
        }
        boolean esNuevo = producto.getId() == null;
        service.guardar(producto);
        ra.addFlashAttribute("mensaje", esNuevo ? "Producto registrado." : "Producto actualizado.");
        return "redirect:/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.eliminar(id);
            ra.addFlashAttribute("mensaje", "Producto eliminado.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }

    private String formulario(Producto producto, Model model) {
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", CATEGORIAS);
        return "productos/formulario";
    }
}
