package org.example.fitgymbackend.modules.reservas.repository;

import org.example.fitgymbackend.modules.reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClaseProgramadaIdOrderByFechaReservaAsc(Long claseId);
    List<Reserva> findByClaseProgramadaIdAndEstadoOrderByFechaReservaAsc(Long claseId, String estado);
    List<Reserva> findBySocioId(Long socioId);
    boolean existsByClaseProgramadaIdAndSocioIdAndEstadoIn(Long claseId, Long socioId, List<String> estados);
}
