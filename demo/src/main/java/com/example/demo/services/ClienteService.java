package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;

@Service
public class ClienteService {

    private final ClienteRepository repo;
    private final PedidoRepository pedidoRepo;

    public ClienteService(ClienteRepository repo, PedidoRepository pedidoRepo) {
        this.repo = repo;
        this.pedidoRepo = pedidoRepo;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) return repo.findAllByOrderByNombreAsc();
        String b = buscar.trim();
        return repo.findByNombreContainingIgnoreCaseOrDniContainingOrderByNombreAsc(b, b);
    }

    @Transactional(readOnly = true)
    public Cliente obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new NegocioException("El cliente con ID " + id + " no existe."));
    }

    @Transactional(readOnly = true)
    public boolean dniEnUso(String dni, Long id) {
        return (id == null) ? repo.existsByDni(dni) : repo.existsByDniAndIdNot(dni, id);
    }

    @Transactional
    public Cliente guardar(Cliente c) {
        c.setNombre(c.getNombre().trim());
        if (c.getTelefono() != null && c.getTelefono().isBlank()) c.setTelefono(null);
        if (c.getDireccion() != null && c.getDireccion().isBlank()) c.setDireccion(null);
        return repo.save(c);
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente c = obtener(id);
        if (pedidoRepo.existsByClienteId(id)) {
            throw new NegocioException("No se puede eliminar a " + c.getNombre() + ": tiene pedidos registrados.");
        }
        repo.delete(c);
    }
}
