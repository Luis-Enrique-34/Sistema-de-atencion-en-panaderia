package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Size(max = 120, message = "Máximo 120 caracteres")
    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "^$|^9\\d{8}$", message = "Celular de 9 dígitos que empiece con 9")
    @Column(length = 9)
    private String telefono;

    @Size(max = 150, message = "Máximo 150 caracteres")
    @Column(length = 150)
    private String direccion;

    @NotNull(message = "Selecciona un rol")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.CLIENTE;

    @Column(nullable = false)
    private boolean activo = true;

    public Usuario() {
    }

    public Usuario(String nombre, String correo, String password, Rol rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }

    public boolean isAdmin() {
        return rol == Rol.ADMIN;
    }

    public boolean isPanadero() {
        return rol == Rol.PANADERO;
    }

    public boolean isCliente() {
        return rol == Rol.CLIENTE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
