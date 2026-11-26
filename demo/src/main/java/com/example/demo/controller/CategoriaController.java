package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Categoria;
import com.example.demo.services.CategoriaService;
import com.example.demo.services.NegocioException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listar());
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("categoria", categoriaService.obtener(id));
            return "categorias/formulario";
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/categorias";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria, BindingResult result,
            RedirectAttributes redirect) {

        if (!result.hasFieldErrors("nombre") && categoriaService.nombreEnUso(categoria.getNombre(), categoria.getId())) {
            result.rejectValue("nombre", "duplicado", "Ya existe una categoría con ese nombre");
        }
        if (result.hasErrors()) {
            return "categorias/formulario";
        }

        boolean esNueva = categoria.getId() == null;
        categoriaService.guardar(categoria);
        redirect.addFlashAttribute("mensaje", esNueva ? "Categoría registrada." : "Categoría actualizada.");
        return "redirect:/admin/categorias";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoriaService.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Categoría eliminada.");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categorias";
    }
}
