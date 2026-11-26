package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
    List<Pedido> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
    List<Pedido> findByEstadoOrderByIdDesc(EstadoPedido estado);

    @EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
    List<Pedido> findByClienteCorreoOrderByIdDesc(String correo);

    @EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
    Optional<Pedido> findConDetallesById(Long id);

    long countByEstado(EstadoPedido estado);

    long countByClienteCorreo(String correo);

    boolean existsByClienteId(Long clienteId);
}
