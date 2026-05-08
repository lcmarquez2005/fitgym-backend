package org.example.fitgymbackend.modules.reservas.repository;

import org.example.fitgymbackend.modules.reservas.entity.ClaseProgramada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClaseProgramadaRepository extends JpaRepository<ClaseProgramada, Long> {
    List<ClaseProgramada> findByFechaBetweenOrderByFechaAscHoraInicioAsc(LocalDate start, LocalDate end);
    List<ClaseProgramada> findByInstructorIdAndFechaBetween(Long instructorId, LocalDate start, LocalDate end);
}
