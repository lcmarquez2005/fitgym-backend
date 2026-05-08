package org.example.fitgymbackend.modules.reservas.repository;

import org.example.fitgymbackend.modules.reservas.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {
    List<Salon> findByActivoTrue();
}
