package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasCategoriaDTO {
    private String periodo;
    private BigDecimal totalVentas;
    private Map<String, BigDecimal> ventasPorCategoria;
    private Map<String, Double> porcentajesPorCategoria;
}
