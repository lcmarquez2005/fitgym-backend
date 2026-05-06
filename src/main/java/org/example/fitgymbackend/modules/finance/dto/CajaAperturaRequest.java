package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CajaAperturaRequest {
    private BigDecimal saldoInicial;
}
