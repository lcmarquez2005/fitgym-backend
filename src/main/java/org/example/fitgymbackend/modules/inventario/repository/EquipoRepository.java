package org.example.fitgymbackend.modules.inventario.repository;

import org.example.fitgymbackend.modules.inventario.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    List<Equipo> findByEstado(String estado);
    List<Equipo> findByCategoriaId(Long categoriaId);
}
