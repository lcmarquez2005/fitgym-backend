package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GenerarNominaRequest {
    private String periodo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
