package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByCorteCajaId(Long corteCajaId);

    @Query("SELECT t FROM Transaccion t WHERE t.tipo = :tipo AND t.fechaHora >= :startDate AND t.fechaHora < :endDate")
    List<Transaccion> findByTipoAndFechaBetween(@Param("tipo") String tipo, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaccion t WHERE t.empleadoId = :empleadoId AND t.fechaHora >= :startDate AND t.fechaHora <= :endDate")
    List<Transaccion> findByEmpleadoIdAndFechaHoraBetween(@Param("empleadoId") Long empleadoId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
