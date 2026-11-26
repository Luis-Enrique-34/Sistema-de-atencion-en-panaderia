package com.example.demo.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.DetallePedido;
import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Pedido;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar(EstadoPedido estado) {
        return estado == null
                ? pedidoRepository.findAllByOrderByIdDesc()
                : pedidoRepository.findByEstadoOrderByIdDesc(estado);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarDeCliente(String correo) {
        return pedidoRepository.findByClienteCorreoOrderByIdDesc(correo);
    }

    @Transactional(readOnly = true)
    public long contar(EstadoPedido estado) {
        return pedidoRepository.countByEstado(estado);
    }

    @Transactional(readOnly = true)
    public long contarDeCliente(String correo) {
        return pedidoRepository.countByClienteCorreo(correo);
    }

    @Transactional(readOnly = true)
    public Pedido obtener(Long id) {
        return pedidoRepository.findConDetallesById(id)
                .orElseThrow(() -> new NegocioException("El pedido N° " + id + " no existe."));
    }

    @Transactional(readOnly = true)
    public Pedido obtenerDeCliente(Long id, String correo) {
        Pedido pedido = obtener(id);
        if (!pedido.getCliente().getCorreo().equals(correo)) {
            throw new NegocioException("El pedido N° " + id + " no existe.");
        }
        return pedido;
    }

    @Transactional
    public Pedido crear(String correo, List<Long> productoIds, List<Integer> cantidades,
            LocalDate fechaEntrega, String observaciones) {

        if (fechaEntrega == null || fechaEntrega.isBefore(LocalDate.now())) {
            throw new NegocioException("Elige una fecha de entrega de hoy en adelante.");
        }

        Usuario cliente = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new NegocioException("El cliente no existe."));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());
        pedido.setFechaEntrega(fechaEntrega);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setObservaciones(observaciones == null || observaciones.isBlank() ? null : observaciones.trim());

        BigDecimal total = BigDecimal.ZERO;
        if (productoIds != null && cantidades != null) {
            for (int i = 0; i < productoIds.size() && i < cantidades.size(); i++) {
                Integer cantidad = cantidades.get(i);
                if (cantidad == null || cantidad <= 0) {
                    continue;
                }
                Producto producto = productoRepository.findById(productoIds.get(i))
                        .orElseThrow(() -> new NegocioException("Uno de los panes elegidos ya no existe."));
                if (producto.getStock() < cantidad) {
                    throw new NegocioException("Solo quedan " + producto.getStock() + " unidades de " + producto.getNombre() + ".");
                }
                producto.setStock(producto.getStock() - cantidad);

                DetallePedido detalle = new DetallePedido(producto, cantidad);
                pedido.agregarDetalle(detalle);
                total = total.add(detalle.getSubtotal());
            }
        }

        if (pedido.getDetalles().isEmpty()) {
            throw new NegocioException("Agrega al menos un tipo de pan a tu pedido.");
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido avanzarEstado(Long id) {
        Pedido pedido = obtener(id);
        EstadoPedido siguiente = pedido.getEstado().getSiguiente();
        if (siguiente == null) {
            throw new NegocioException("El pedido N° " + id + " ya está " + pedido.getEstado().getEtiqueta() + ".");
        }
        pedido.setEstado(siguiente);
        return pedido;
    }

    @Transactional
    public void cancelar(Long id) {
        Pedido pedido = obtener(id);
        if (!pedido.isCancelable()) {
            throw new NegocioException("El pedido N° " + id + " ya no se puede cancelar.");
        }
        devolverStock(pedido);
    }

    @Transactional
    public void cancelarDeCliente(Long id, String correo) {
        Pedido pedido = obtenerDeCliente(id, correo);
        if (!pedido.isPendiente()) {
            throw new NegocioException("Solo puedes cancelar pedidos pendientes. Este ya está " + pedido.getEstado().getEtiqueta() + ".");
        }
        devolverStock(pedido);
    }

    private void devolverStock(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
    }
}
