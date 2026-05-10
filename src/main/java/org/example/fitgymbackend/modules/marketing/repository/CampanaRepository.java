// CampanaRepository.java
package org.example.fitgymbackend.modules.marketing.repository;

import org.example.fitgymbackend.modules.marketing.entity.Campana;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CampanaRepository extends JpaRepository<Campana, Long> {

    List<Campana> findAllByOrderByFechaCreacionDesc();

    List<Campana> findByEstado(String estado);

    List<Campana> findByTipo(String tipo);

    List<Campana> findByEstadoAndFechaProgramadaBefore(String estado, LocalDateTime fecha);
}