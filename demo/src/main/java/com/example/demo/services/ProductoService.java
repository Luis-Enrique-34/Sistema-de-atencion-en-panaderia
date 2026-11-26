package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Categoria;
import com.example.demo.model.Producto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class ProductoService {

    private static final int STOCK_MINIMO = 20;

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
            DetallePedidoRepository detallePedidoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return productoRepository.findAllByOrderByCategoriaNombreAscNombreAsc();
        }
        return productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(buscar.trim());
    }

    @Transactional(readOnly = true)
    public List<Producto> listarDisponibles() {
        return productoRepository.findByStockGreaterThanOrderByCategoriaNombreAscNombreAsc(0);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarStockBajo() {
        return productoRepository.findByStockLessThanOrderByStockAsc(STOCK_MINIMO);
    }

    @Transactional(readOnly = true)
    public Producto obtener(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NegocioException("El producto no existe."));
    }

    @Transactional(readOnly = true)
    public boolean nombreEnUso(String nombre, Long id) {
        String limpio = nombre.trim();
        return id == null
                ? productoRepository.existsByNombreIgnoreCase(limpio)
                : productoRepository.existsByNombreIgnoreCaseAndIdNot(limpio, id);
    }

    @Transactional
    public Producto guardar(Producto producto) {
        Categoria categoria = categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new NegocioException("La categoría seleccionada no existe."));
        producto.setCategoria(categoria);
        producto.setNombre(producto.getNombre().trim());
        if (producto.getDescripcion() != null && producto.getDescripcion().isBlank()) {
            producto.setDescripcion(null);
        }
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = obtener(id);
        if (detallePedidoRepository.existsByProductoId(id)) {
            throw new NegocioException(producto.getNombre() + " ya aparece en pedidos. Pon su stock en 0 para ocultarlo.");
        }
        productoRepository.delete(producto);
    }
}
