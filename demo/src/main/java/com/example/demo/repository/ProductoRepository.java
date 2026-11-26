package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findAllByOrderByCategoriaNombreAscNombreAsc();

    List<Producto> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    List<Producto> findByStockGreaterThanOrderByCategoriaNombreAscNombreAsc(Integer stock);

    List<Producto> findByStockLessThanOrderByStockAsc(Integer stock);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    boolean existsByCategoriaId(Long categoriaId);
}
