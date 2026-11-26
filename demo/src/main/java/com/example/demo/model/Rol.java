package com.example.demo.model;

public enum Rol {

    ADMIN("Administrador"),
    PANADERO("Panadero"),
    CLIENTE("Cliente");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
