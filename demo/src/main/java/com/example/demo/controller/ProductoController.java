package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Categoria;
import com.example.demo.model.Producto;
import com.example.demo.services.CategoriaService;
import com.example.demo.services.NegocioException;
import com.example.demo.services.ProductoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        model.addAttribute("productos", productoService.listar(buscar));
        model.addAttribute("buscar", buscar);
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Producto producto = new Producto();
        producto.setCategoria(new Categoria());
        return mostrarFormulario(producto, model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            return mostrarFormulario(productoService.obtener(id), model);
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") Producto producto, BindingResult result,
            Model model, RedirectAttributes redirect) {

        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            result.rejectValue("categoria", "requerida", "Selecciona una categoría");
        }
        if (!result.hasFieldErrors("nombre") && productoService.nombreEnUso(producto.getNombre(), producto.getId())) {
            result.rejectValue("nombre", "duplicado", "Ya existe un producto con ese nombre");
        }
        if (result.hasErrors()) {
            if (producto.getCategoria() == null) {
                producto.setCategoria(new Categoria());
            }
            return mostrarFormulario(producto, model);
        }

        boolean esNuevo = producto.getId() == null;
        productoService.guardar(producto);
        redirect.addFlashAttribute("mensaje", esNuevo ? "Producto registrado." : "Producto actualizado.");
        return "redirect:/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            productoService.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Producto eliminado.");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }

    private String mostrarFormulario(Producto producto, Model model) {
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listar());
        return "productos/formulario";
    }
}
