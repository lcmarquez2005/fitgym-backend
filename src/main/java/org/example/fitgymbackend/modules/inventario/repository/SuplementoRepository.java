package org.example.fitgymbackend.modules.inventario.repository;

import org.example.fitgymbackend.modules.inventario.entity.Suplemento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SuplementoRepository extends JpaRepository<Suplemento, Long> {
    List<Suplemento> findByActivoTrue();
    // Alerta: stock actual <= stockMinimo
    List<Suplemento> findByActivoTrueAndStockLessThanEqual(Integer stockMinimo);
}
