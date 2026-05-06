package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {
}
