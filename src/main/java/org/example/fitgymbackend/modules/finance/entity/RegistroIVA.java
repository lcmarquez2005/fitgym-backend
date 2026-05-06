package org.example.fitgymbackend.modules.finance.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Registro de IVA del periodo:
 *   - IVA Trasladado: lo que cobramos al cliente (16% sobre ventas)
 *   - IVA Acreditable: lo que pagamos a proveedores (podemos deducirlo)
 *   - IVA a Pagar = Trasladado - Acreditable
 */
@Data
@Entity
@Table(name = "finance_registro_iva")
public class RegistroIVA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("periodo-iva")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_fiscal_id")
    private PeriodoFiscal periodiFiscal;

    private BigDecimal baseGravable;      // Total de ingresos gravados (sin IVA)
    private BigDecimal ivaTrasladado;     // baseGravable * 0.16
    private BigDecimal ivaAcreditable;   // IVA pagado a proveedores
    private BigDecimal ivaNeto;          // ivaTrasladado - ivaAcreditable (positivo = a pagar, negativo = a favor)

    private String observaciones;
}
