package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RetencionISRRequest {
    private Long periodoFiscalId;
    private String tipoRetencion;       // SUELDOS, ASIMILADOS, ARRENDAMIENTO
    private String nombreBeneficiario;
    private String rfcBeneficiario;
    private BigDecimal montoBase;
    private BigDecimal tasaAplicada;    // 0.10 = 10%
}
