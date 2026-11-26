package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Pedido;
import com.example.demo.services.NegocioException;
import com.example.demo.services.PedidoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoPedido estado, Model model) {
        model.addAttribute("pedidos", pedidoService.listar(estado));
        model.addAttribute("estado", estado);
        model.addAttribute("estados", EstadoPedido.values());
        return "pedidos/lista";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("pedido", pedidoService.obtener(id));
            model.addAttribute("esPersonal", true);
            return "pedidos/orden";
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedidos";
        }
    }

    @PostMapping("/{id}/avanzar")
    public String avanzar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Pedido pedido = pedidoService.avanzarEstado(id);
            redirect.addFlashAttribute("mensaje", "Pedido N° " + id + " ahora está: " + pedido.getEstado().getEtiqueta() + ".");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            pedidoService.cancelar(id);
            redirect.addFlashAttribute("mensaje", "Pedido N° " + id + " cancelado. El stock fue devuelto.");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pedidos";
    }
}
