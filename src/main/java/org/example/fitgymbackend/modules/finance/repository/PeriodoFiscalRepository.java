package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.PeriodoFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PeriodoFiscalRepository extends JpaRepository<PeriodoFiscal, Long> {
    List<PeriodoFiscal> findByEstadoOrderByFechaLimiteAsc(String estado);
    List<PeriodoFiscal> findByTipoPeriodo(String tipoPeriodo);
}
