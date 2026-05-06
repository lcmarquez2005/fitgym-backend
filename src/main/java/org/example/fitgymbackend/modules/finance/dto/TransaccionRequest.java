package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransaccionRequest {
    private String tipo; // INGRESO, EGRESO
    private String categoria;
    private BigDecimal monto;
    private String descripcion;
    private Boolean requiereFactura;
}
