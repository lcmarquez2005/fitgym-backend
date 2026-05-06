package org.example.fitgymbackend.modules.finance.repository;

import org.example.fitgymbackend.modules.finance.entity.RegistroDIOT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegistroDIOTRepository extends JpaRepository<RegistroDIOT, Long> {
    List<RegistroDIOT> findByMesDeclaracion(String mes);
}
