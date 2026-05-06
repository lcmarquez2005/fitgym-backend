package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
}
