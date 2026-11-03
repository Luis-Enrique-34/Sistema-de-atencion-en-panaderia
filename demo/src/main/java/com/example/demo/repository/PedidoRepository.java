package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Trae cliente y producto en la misma consulta (evita N+1 consultas)
    @EntityGraph(attributePaths = {"cliente", "producto"})
    List<Pedido> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"cliente", "producto"})
    List<Pedido> findByEstadoOrderByIdDesc(EstadoPedido estado);

    boolean existsByClienteId(Long clienteId);

    boolean existsByProductoId(Long productoId);
}
