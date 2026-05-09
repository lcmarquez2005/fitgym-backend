package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportePredictivoDTO {
    private String periodo;
    
    // Churn Rate
    private Integer sociosInicioMes;
    private Integer sociosCancelados;
    private Double churnRatePorcentaje;
    
    // LTV / CAC
    private BigDecimal lifetimeValue; // LTV
    private BigDecimal customerAcquisitionCost; // CAC
    private Double ratioLtvCac;
    
    // Break-even
    private BigDecimal costosFijosEstimados;
    private BigDecimal margenContribucionPorcentaje;
    private BigDecimal puntoEquilibrio; // Ingresos necesarios para no perder dinero
}
