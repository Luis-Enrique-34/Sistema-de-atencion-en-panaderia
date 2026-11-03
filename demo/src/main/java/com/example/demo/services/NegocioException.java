package com.example.demo.services;

// Error de regla de negocio: su mensaje se muestra tal cual al usuario
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
