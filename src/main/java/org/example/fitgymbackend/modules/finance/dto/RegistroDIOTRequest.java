package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegistroDIOTRequest {
    private String mesDeclaracion;      // "Mayo 2026"
    private String rfcProveedor;
    private String nombreProveedor;
    private String tipoProveedor;       // NACIONAL, EXTRANJERO
    private BigDecimal montoOperacion;
    private BigDecimal ivaAcreditable;
    private String concepto;
}
