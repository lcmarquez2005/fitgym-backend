package org.example.fitgymbackend.modules.finance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "finance_transaccion")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo; // INGRESO, EGRESO
    private String categoria; // MEMBRESIA, SUPLEMENTO, MANTENIMIENTO, etc.
    
    private BigDecimal monto;
    private LocalDateTime fechaHora;
    private String descripcion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corte_caja_id")
    private CorteCaja corteCaja;
    
    private Boolean requiereFactura = false;
    
    @Column(name = "empleado_id")
    private Long empleadoId; // ID del empleado que realizó la venta (para comisiones)
}
