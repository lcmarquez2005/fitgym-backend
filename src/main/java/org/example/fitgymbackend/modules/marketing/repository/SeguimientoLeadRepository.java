// SeguimientoLeadRepository.java
package org.example.fitgymbackend.modules.marketing.repository;

import org.example.fitgymbackend.modules.marketing.entity.SeguimientoLead;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeguimientoLeadRepository extends JpaRepository<SeguimientoLead, Long> {

    List<SeguimientoLead> findByLeadIdOrderByFechaContactoDesc(Long leadId);
}