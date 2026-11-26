package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Categoria;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Categoria obtener(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NegocioException("La categoría no existe."));
    }

    @Transactional(readOnly = true)
    public boolean nombreEnUso(String nombre, Long id) {
        String limpio = nombre.trim();
        return id == null
                ? categoriaRepository.existsByNombreIgnoreCase(limpio)
                : categoriaRepository.existsByNombreIgnoreCaseAndIdNot(limpio, id);
    }

    @Transactional
    public Categoria guardar(Categoria categoria) {
        categoria.setNombre(categoria.getNombre().trim());
        if (categoria.getDescripcion() != null && categoria.getDescripcion().isBlank()) {
            categoria.setDescripcion(null);
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = obtener(id);
        if (productoRepository.existsByCategoriaId(id)) {
            throw new NegocioException("La categoría " + categoria.getNombre() + " tiene productos asignados.");
        }
        categoriaRepository.delete(categoria);
    }
}
