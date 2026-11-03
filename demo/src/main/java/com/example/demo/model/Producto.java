package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "Máximo 80 caracteres")
    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @NotBlank(message = "Selecciona una categoría")
    @Column(nullable = false, length = 40)
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.10", message = "El precio mínimo es S/ 0.10")
    @Digits(integer = 6, fraction = 2, message = "Máximo 2 decimales")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Size(max = 255, message = "Máximo 255 caracteres")
    private String descripcion;

    public Producto() {}

    public Producto(String nombre, String categoria, String precio, Integer stock, String descripcion) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = new BigDecimal(precio);
        this.stock = stock;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
