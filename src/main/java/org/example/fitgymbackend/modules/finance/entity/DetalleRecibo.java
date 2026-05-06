package org.example.fitgymbackend.modules.finance.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "finance_detalle_recibo")
public class DetalleRecibo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("recibo-detalles")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_nomina_id")
    private ReciboNomina reciboNomina;

    private String tipo; // PERCEPCION, DEDUCCION
    private String concepto; // "Sueldo base", "ISR", "Comisión Ventas"
    private BigDecimal monto;
}
