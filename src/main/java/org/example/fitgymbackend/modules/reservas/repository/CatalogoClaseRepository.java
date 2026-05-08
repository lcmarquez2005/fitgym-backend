package org.example.fitgymbackend.modules.reservas.repository;

import org.example.fitgymbackend.modules.reservas.entity.CatalogoClase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogoClaseRepository extends JpaRepository<CatalogoClase, Long> {
    List<CatalogoClase> findByActivoTrue();
}
