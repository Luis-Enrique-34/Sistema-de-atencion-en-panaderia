package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findAllByOrderByNombreAsc();

    List<Cliente> findByNombreContainingIgnoreCaseOrDniContainingOrderByNombreAsc(String nombre, String dni);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);
}
