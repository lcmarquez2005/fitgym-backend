package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCuentasCobrarDTO {
    private String fechaGeneracion;
    private BigDecimal montoTotalAdeudado;
    private Integer cantidadDeudores;
    
    private List<DeudorDTO> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeudorDTO {
        private Long socioId;
        private String nombreCompleto;
        private String telefono;
        private String fechaVencimiento;
        private BigDecimal montoAdeudado;
    }
}
