package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteFlujoEfectivoDTO {

    private String periodo;
    private String fechaInicio;
    private String fechaFin;

    private BigDecimal saldoInicial;
    
    // Entradas de efectivo (Ingresos)
    private BigDecimal totalEntradas;
    private Map<String, BigDecimal> desgloseEntradas;
    
    // Salidas de efectivo (Egresos)
    private BigDecimal totalSalidas;
    private Map<String, BigDecimal> desgloseSalidas;

    // Flujo Neto = Entradas - Salidas
    private BigDecimal flujoNeto;

    // Saldo Final = Saldo Inicial + Flujo Neto
    private BigDecimal saldoFinal;
}
