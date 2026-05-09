package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteOperacionesDiariasDTO {
    private String fecha;
    private BigDecimal cajaInicial;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal cajaFinalCalculada;
    
    private Map<String, BigDecimal> ingresosPorCategoria;
    private Map<String, BigDecimal> egresosPorCategoria;
    
    private Integer transaccionesTotales;
}
