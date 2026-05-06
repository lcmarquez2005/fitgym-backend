package org.example.fitgymbackend.modules.finance.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Retención ISR por tipo:
 *   - SUELDOS: retenido de la nómina de empleados
 *   - ASIMILADOS: honorarios de entrenadores freelance / nutriólogos
 *   - ARRENDAMIENTO: ISR sobre la renta del local
 */
@Data
@Entity
@Table(name = "finance_retencion_isr")
public class RetencionISR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("periodo-isr")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_fiscal_id")
    private PeriodoFiscal periodiFiscal;

    private String tipoRetencion;     // SUELDOS, ASIMILADOS, ARRENDAMIENTO
    private String nombreBeneficiario;
    private String rfcBeneficiario;
    private BigDecimal montoBase;     // Monto sobre el que se calcula el ISR
    private BigDecimal tasaAplicada;  // Ej: 0.10 para 10%
    private BigDecimal montoISR;      // montoBase * tasaAplicada
}
