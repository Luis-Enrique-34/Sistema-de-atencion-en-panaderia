package com.example.demo.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PedidoForm;
import com.example.demo.model.Cliente;
import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Pedido;
import com.example.demo.model.Producto;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final ClienteRepository clienteRepo;
    private final ProductoRepository productoRepo;

    public PedidoService(PedidoRepository repo, ClienteRepository clienteRepo, ProductoRepository productoRepo) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
        this.productoRepo = productoRepo;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar(EstadoPedido estado) {
        return (estado == null) ? repo.findAllByOrderByIdDesc() : repo.findByEstadoOrderByIdDesc(estado);
    }

    @Transactional(readOnly = true)
    public Pedido obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new NegocioException("El pedido N° " + id + " no existe."));
    }

    // Convierte un pedido existente al formulario (para editar)
    @Transactional(readOnly = true)
    public PedidoForm aFormulario(Long id) {
        Pedido p = obtener(id);
        if (!p.isPendiente()) throw new NegocioException("Solo se pueden editar pedidos PENDIENTES.");
        PedidoForm f = new PedidoForm();
        f.setId(p.getId());
        f.setClienteId(p.getCliente().getId());
        f.setProductoId(p.getProducto().getId());
        f.setCantidad(p.getCantidad());
        f.setObservacion(p.getObservacion());
        return f;
    }

    // Genera una orden nueva o edita una pendiente, actualizando el stock
    @Transactional
    public Pedido guardar(PedidoForm f) {
        Cliente cliente = clienteRepo.findById(f.getClienteId())
                .orElseThrow(() -> new NegocioException("El cliente seleccionado no existe."));
        Producto producto = productoRepo.findById(f.getProductoId())
                .orElseThrow(() -> new NegocioException("El producto seleccionado no existe."));

        Pedido p;
        if (f.getId() == null) {
            p = new Pedido();
            p.setFecha(LocalDateTime.now());
            p.setEstado(EstadoPedido.PENDIENTE);
        } else {
            p = obtener(f.getId());
            if (!p.isPendiente()) throw new NegocioException("Solo se pueden editar pedidos PENDIENTES.");
            devolverStock(p); // se devuelve lo reservado antes de recalcular
        }

        if (producto.getStock() < f.getCantidad()) {
            // la excepción revierte toda la transacción (incluida la devolución de stock)
            throw new NegocioException("Stock insuficiente de " + producto.getNombre()
                    + ". Disponible: " + producto.getStock() + ".");
        }
        producto.setStock(producto.getStock() - f.getCantidad());

        p.setCliente(cliente);
        p.setProducto(producto);
        p.setCantidad(f.getCantidad());
        p.setPrecioUnitario(producto.getPrecio());
        p.setTotal(producto.getPrecio().multiply(BigDecimal.valueOf(f.getCantidad())));
        p.setObservacion((f.getObservacion() == null || f.getObservacion().isBlank()) ? null : f.getObservacion().trim());
        return repo.save(p);
    }

    @Transactional
    public void entregar(Long id) {
        Pedido p = obtenerPendiente(id);
        p.setEstado(EstadoPedido.ENTREGADO);
    }

    @Transactional
    public void anular(Long id) {
        Pedido p = obtenerPendiente(id);
        devolverStock(p);
        p.setEstado(EstadoPedido.ANULADO);
    }

    @Transactional
    public void eliminar(Long id) {
        Pedido p = obtener(id);
        if (p.isPendiente()) {
            throw new NegocioException("Primero anula o entrega el pedido N° " + id + " antes de eliminarlo.");
        }
        repo.delete(p);
    }

    private Pedido obtenerPendiente(Long id) {
        Pedido p = obtener(id);
        if (!p.isPendiente()) throw new NegocioException("El pedido N° " + id + " ya está " + p.getEstado() + ".");
        return p;
    }

    private void devolverStock(Pedido p) {
        Producto prod = p.getProducto();
        prod.setStock(prod.getStock() + p.getCantidad());
    }
}
