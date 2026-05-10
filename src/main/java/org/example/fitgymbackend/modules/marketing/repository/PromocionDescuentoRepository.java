// PromocionDescuentoRepository.java
package org.example.fitgymbackend.modules.marketing.repository;

import org.example.fitgymbackend.modules.marketing.entity.PromocionDescuento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PromocionDescuentoRepository extends JpaRepository<PromocionDescuento, Long> {

    Optional<PromocionDescuento> findByCodigo(String codigo);

    List<PromocionDescuento> findByActivoTrue();

    List<PromocionDescuento> findAllByOrderByFechaCreacionDesc();

    boolean existsByCodigo(String codigo);
}