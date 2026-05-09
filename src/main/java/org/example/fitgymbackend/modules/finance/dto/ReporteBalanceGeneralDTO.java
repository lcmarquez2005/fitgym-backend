package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteBalanceGeneralDTO {

    private String periodo;
    private String fechaGeneracion;

    private Activos activos;
    private Pasivos pasivos;
    private CapitalContable capitalContable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Activos {
        private BigDecimal circulanteCaja;
        private BigDecimal circulanteInventario;
        private BigDecimal circulanteTotal;
        
        // Mock values for now
        private BigDecimal fijoMaquinaria;
        private BigDecimal fijoMobiliario;
        private BigDecimal fijoTotal;
        
        private BigDecimal total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pasivos {
        // Mock values for now
        private BigDecimal cortoPlazoProveedores;
        private BigDecimal largoPlazoPrestamos;
        private BigDecimal total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapitalContable {
        // Mock values for now
        private BigDecimal capitalSocial;
        private BigDecimal utilidadesRetenidas;
        private BigDecimal total;
    }
}
