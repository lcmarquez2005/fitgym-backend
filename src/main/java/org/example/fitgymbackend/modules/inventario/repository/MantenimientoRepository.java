package org.example.fitgymbackend.modules.inventario.repository;

import org.example.fitgymbackend.modules.inventario.entity.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    List<Mantenimiento> findByEquipoIdOrderByFechaDesc(Long equipoId);
}
