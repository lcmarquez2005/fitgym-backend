package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.CorteCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorteCajaRepository extends JpaRepository<CorteCaja, Long> {
    Optional<CorteCaja> findFirstByEstadoOrderByIdDesc(String estado);
}
