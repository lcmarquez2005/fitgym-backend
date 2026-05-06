package org.example.fitgymbackend.modules.inventario.repository;

import org.example.fitgymbackend.modules.inventario.entity.CategoriaEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaEquipoRepository extends JpaRepository<CategoriaEquipo, Long> {}
