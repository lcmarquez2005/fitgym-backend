package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.RetencionISR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RetencionISRRepository extends JpaRepository<RetencionISR, Long> {
    List<RetencionISR> findByPeriodiFiscalId(Long periodoId);
}
