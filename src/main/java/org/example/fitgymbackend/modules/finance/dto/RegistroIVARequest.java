package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegistroIVARequest {
    private Long periodoFiscalId;
    private BigDecimal ivaAcreditable;   // IVA pagado a proveedores (manual)
    private String observaciones;
}
