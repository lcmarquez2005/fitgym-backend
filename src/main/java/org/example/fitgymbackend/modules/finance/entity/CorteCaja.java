package org.example.fitgymbackend.modules.finance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "finance_corte_caja")
public class CorteCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraApertura;
    private LocalDateTime fechaHoraCierre;
    
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinal;
    
    @Column(name = "usuario_id_apertura")
    private Integer usuarioIdApertura;
    
    @Column(name = "estado")
    private String estado; // ABIERTA, CERRADA
}
