package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Producto;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final PedidoRepository pedidoRepo;

    public ProductoService(ProductoRepository repo, PedidoRepository pedidoRepo) {
        this.repo = repo;
        this.pedidoRepo = pedidoRepo;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar(String buscar) {
        return (buscar == null || buscar.isBlank())
                ? repo.findAllByOrderByIdAsc()
                : repo.findByNombreContainingIgnoreCaseOrderByIdAsc(buscar.trim());
    }

    @Transactional(readOnly = true)
    public Producto obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new NegocioException("El producto con ID " + id + " no existe."));
    }

    @Transactional(readOnly = true)
    public boolean nombreEnUso(String nombre, Long id) {
        String n = nombre.trim();
        return (id == null) ? repo.existsByNombreIgnoreCase(n) : repo.existsByNombreIgnoreCaseAndIdNot(n, id);
    }

    @Transactional
    public Producto guardar(Producto p) {
        p.setNombre(p.getNombre().trim());
        if (p.getDescripcion() != null && p.getDescripcion().isBlank()) p.setDescripcion(null);
        return repo.save(p);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto p = obtener(id);
        if (pedidoRepo.existsByProductoId(id)) {
            throw new NegocioException("No se puede eliminar \"" + p.getNombre() + "\": tiene pedidos registrados.");
        }
        repo.delete(p);
    }
}
