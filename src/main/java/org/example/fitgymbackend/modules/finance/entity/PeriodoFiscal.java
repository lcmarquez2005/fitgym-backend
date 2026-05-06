package org.example.fitgymbackend.modules.finance.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un periodo fiscal (mes/bimestre/año) con su estado de presentación.
 */
@Data
@Entity
@Table(name = "finance_periodo_fiscal")
public class PeriodoFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;           // "Mayo 2026", "Bimestre 3 - 2026", "Anual 2026"
    private String tipoPeriodo;      // MENSUAL, BIMESTRAL, ANUAL
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaLimite;   // Fecha límite de presentación ante el SAT
    private String estado;           // PENDIENTE, PRESENTADA, VENCIDA

    @JsonManagedReference("periodo-iva")
    @OneToMany(mappedBy = "periodiFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroIVA> registrosIVA = new ArrayList<>();

    @JsonManagedReference("periodo-isr")
    @OneToMany(mappedBy = "periodiFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RetencionISR> retencionesISR = new ArrayList<>();
}
