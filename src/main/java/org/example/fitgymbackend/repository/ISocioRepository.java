package org.example.fitgymbackend.repository;

import org.example.fitgymbackend.entity.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ISocioRepository extends JpaRepository<Socio, Long> {

    @Query("SELECT s FROM Socio s WHERE " +
            "LOWER(s.nombreCompleto) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.email)         LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.idSocio)       LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Socio> buscar(@Param("q") String q);
}