package com.example.demo.model;

public enum EstadoPedido {

    PENDIENTE("Pendiente", "warning"),
    EN_PREPARACION("En preparación", "info"),
    LISTO("Listo para entregar", "primary"),
    ENTREGADO("Entregado", "success"),
    CANCELADO("Cancelado", "secondary");

    private final String etiqueta;
    private final String color;

    EstadoPedido(String etiqueta, String color) {
        this.etiqueta = etiqueta;
        this.color = color;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColor() {
        return color;
    }

    public EstadoPedido getSiguiente() {
        return switch (this) {
            case PENDIENTE -> EN_PREPARACION;
            case EN_PREPARACION -> LISTO;
            case LISTO -> ENTREGADO;
            default -> null;
        };
    }
}
