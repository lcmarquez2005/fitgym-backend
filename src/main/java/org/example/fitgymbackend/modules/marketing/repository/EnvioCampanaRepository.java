// EnvioCampanaRepository.java
package org.example.fitgymbackend.modules.marketing.repository;

import org.example.fitgymbackend.modules.marketing.entity.EnvioCampana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EnvioCampanaRepository extends JpaRepository<EnvioCampana, Long> {

    List<EnvioCampana> findByCampanaId(Long campanaId);

    boolean existsByCampanaIdAndEmailDestinatario(Long campanaId, String email);

    @Query("SELECT COUNT(e) FROM EnvioCampana e WHERE e.campana.id = :id AND e.abierto = true")
    long contarAbiertos(@Param("id") Long campanaId);

    @Query("SELECT COUNT(e) FROM EnvioCampana e WHERE e.campana.id = :id")
    long contarTotales(@Param("id") Long campanaId);
}