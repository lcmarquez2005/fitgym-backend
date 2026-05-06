package org.example.fitgymbackend.modules.inventario.repository;

import org.example.fitgymbackend.modules.inventario.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByActivoTrue();
}
