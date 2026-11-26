package com.example.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Pedido;
import com.example.demo.services.NegocioException;
import com.example.demo.services.PedidoService;
import com.example.demo.services.ProductoService;

@Controller
@RequestMapping("/tienda")
public class TiendaController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;

    public TiendaController(ProductoService productoService, PedidoService pedidoService) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/nuevo-pedido")
    public String nuevoPedido(Model model) {
        model.addAttribute("productos", productoService.listarDisponibles());
        model.addAttribute("hoy", LocalDate.now());
        return "tienda/nuevo-pedido";
    }

    @PostMapping("/pedidos")
    public String crearPedido(@RequestParam(name = "productoId", required = false) List<Long> productoIds,
            @RequestParam(name = "cantidad", required = false) List<Integer> cantidades,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrega,
            @RequestParam(required = false) String observaciones,
            Principal principal, RedirectAttributes redirect) {
        try {
            Pedido pedido = pedidoService.crear(principal.getName(), productoIds, cantidades, fechaEntrega, observaciones);
            redirect.addFlashAttribute("mensaje", "¡Pedido N° " + pedido.getId() + " registrado! Te avisaremos cuando esté listo.");
            return "redirect:/tienda/mis-pedidos/" + pedido.getId();
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/tienda/nuevo-pedido";
        }
    }

    @GetMapping("/mis-pedidos")
    public String misPedidos(Principal principal, Model model) {
        model.addAttribute("pedidos", pedidoService.listarDeCliente(principal.getName()));
        return "tienda/mis-pedidos";
    }

    @GetMapping("/mis-pedidos/{id}")
    public String verPedido(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("pedido", pedidoService.obtenerDeCliente(id, principal.getName()));
            model.addAttribute("esPersonal", false);
            return "pedidos/orden";
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/tienda/mis-pedidos";
        }
    }

    @PostMapping("/mis-pedidos/{id}/cancelar")
    public String cancelar(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {
        try {
            pedidoService.cancelarDeCliente(id, principal.getName());
            redirect.addFlashAttribute("mensaje", "Pedido N° " + id + " cancelado.");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tienda/mis-pedidos";
    }
}
