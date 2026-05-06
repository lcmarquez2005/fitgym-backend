package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PeriodoFiscalRequest {
    private String nombre;
    private String tipoPeriodo;    // MENSUAL, BIMESTRAL, ANUAL
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaLimite;
}
