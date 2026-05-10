// SegmentoMarketingRepository.java
package org.example.fitgymbackend.modules.marketing.repository;

import org.example.fitgymbackend.modules.marketing.entity.SegmentoMarketing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SegmentoMarketingRepository extends JpaRepository<SegmentoMarketing, Long> {

    List<SegmentoMarketing> findAllByOrderByFechaCreacionDesc();
}