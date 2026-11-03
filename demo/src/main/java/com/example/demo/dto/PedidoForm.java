package com.example.demo.dto;

import jakarta.validation.constraints.*;

// Datos que llegan del formulario de pedidos (se elige cliente y producto por su ID)
public class PedidoForm {

    private Long id;

    @NotNull(message = "Selecciona un cliente")
    private Long clienteId;

    @NotNull(message = "Selecciona un producto")
    private Long productoId;

    @NotNull(message = "Ingresa la cantidad")
    @Min(value = 1, message = "Mínimo 1 unidad")
    @Max(value = 1000, message = "Máximo 1000 unidades")
    private Integer cantidad = 1;

    @Size(max = 255, message = "Máximo 255 caracteres")
    private String observacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
