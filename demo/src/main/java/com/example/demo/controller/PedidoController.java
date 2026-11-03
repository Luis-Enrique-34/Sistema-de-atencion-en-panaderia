package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.PedidoForm;
import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Pedido;
import com.example.demo.services.ClienteService;
import com.example.demo.services.NegocioException;
import com.example.demo.services.PedidoService;
import com.example.demo.services.ProductoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    public PedidoController(PedidoService service, ClienteService clienteService, ProductoService productoService) {
        this.service = service;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    // LISTAR (filtro opcional por estado)
    @GetMapping
    public String listar(@RequestParam(required = false) EstadoPedido estado, Model model) {
        List<Pedido> pedidos = service.listar(estado);
        BigDecimal ventas = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("estado", estado);
        model.addAttribute("estados", EstadoPedido.values());
        model.addAttribute("ventas", ventas);
        return "pedidos/lista";
    }

    // GENERAR ORDEN (se puede llegar con cliente o producto ya elegido)
    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long productoId, Model model) {
        PedidoForm f = new PedidoForm();
        f.setClienteId(clienteId);
        f.setProductoId(productoId);
        return formulario(f, model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            return formulario(service.aFormulario(id), model);
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedidos";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("pedido") PedidoForm form, BindingResult result,
            Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return formulario(form, model);
        }
        try {
            boolean esNuevo = form.getId() == null;
            Pedido p = service.guardar(form);
            ra.addFlashAttribute("mensaje", esNuevo
                    ? "Orden N° " + p.getId() + " generada correctamente."
                    : "Orden N° " + p.getId() + " actualizada.");
            return "redirect:/pedidos/" + p.getId();
        } catch (NegocioException e) {
            result.reject("negocio", e.getMessage());
            return formulario(form, model);
        }
    }

    // VER / IMPRIMIR ORDEN
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("p", service.obtener(id));
            return "pedidos/orden";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedidos";
        }
    }

    @PostMapping("/{id}/entregar")
    public String entregar(@PathVariable Long id, RedirectAttributes ra) {
        return accion(() -> service.entregar(id), "Pedido N° " + id + " marcado como ENTREGADO.", ra);
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Long id, RedirectAttributes ra) {
        return accion(() -> service.anular(id), "Pedido N° " + id + " anulado. Se devolvió el stock.", ra);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        return accion(() -> service.eliminar(id), "Pedido N° " + id + " eliminado.", ra);
    }

    // Ejecuta una acción y deja el mensaje de éxito o de error
    private String accion(Runnable r, String ok, RedirectAttributes ra) {
        try {
            r.run();
            ra.addFlashAttribute("mensaje", ok);
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pedidos";
    }

    private String formulario(PedidoForm form, Model model) {
        model.addAttribute("pedido", form);
        model.addAttribute("clientes", clienteService.listar(null));
        model.addAttribute("productos", productoService.listar(null));
        return "pedidos/formulario";
    }
}
