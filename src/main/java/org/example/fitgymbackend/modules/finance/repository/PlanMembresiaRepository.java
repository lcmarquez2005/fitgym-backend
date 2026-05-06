package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.PlanMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanMembresiaRepository extends JpaRepository<PlanMembresia, Long> {
    List<PlanMembresia> findByActivoTrue();
}
