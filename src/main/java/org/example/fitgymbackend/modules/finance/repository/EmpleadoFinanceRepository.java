package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.EmpleadoFinance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoFinanceRepository extends JpaRepository<EmpleadoFinance, Long> {
    List<EmpleadoFinance> findByActivoTrue();
    Optional<EmpleadoFinance> findByUsuarioId(Integer usuarioId);
    boolean existsByUsuarioId(Integer usuarioId);
}
