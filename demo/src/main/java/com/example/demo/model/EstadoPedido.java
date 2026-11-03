package com.example.demo.model;

public enum EstadoPedido {
    PENDIENTE("warning"),
    ENTREGADO("success"),
    ANULADO("secondary");

    private final String color; // color del badge de Bootstrap

    EstadoPedido(String color) { this.color = color; }

    public String getColor() { return color; }
}
