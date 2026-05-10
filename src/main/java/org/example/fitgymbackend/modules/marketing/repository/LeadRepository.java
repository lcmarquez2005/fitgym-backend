// LeadRepository.java
package org.example.fitgymbackend.modules.marketing.repository;

import org.example.fitgymbackend.modules.marketing.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByEtapaOrderByFechaCreacionDesc(String etapa);

    List<Lead> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT l FROM Lead l WHERE l.etapa NOT IN ('CERRADO','PERDIDO') " +
           "AND l.fechaUltimoContacto < :limite ORDER BY l.fechaUltimoContacto ASC")
    List<Lead> findLeadsSinContactoReciente(@Param("limite") LocalDateTime limite);

    @Query("SELECT l.fuente, COUNT(l) FROM Lead l GROUP BY l.fuente")
    List<Object[]> contarPorFuente();

    @Query("SELECT l.etapa, COUNT(l) FROM Lead l GROUP BY l.etapa")
    List<Object[]> contarPorEtapa();

    long countByEtapa(String etapa);
}