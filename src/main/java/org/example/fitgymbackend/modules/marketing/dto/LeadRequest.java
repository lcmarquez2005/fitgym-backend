// LeadRequest.java
package org.example.fitgymbackend.modules.marketing.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LeadRequest {
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String fuente;
    private String notas;
    private String asignadoA;
    private LocalDate fechaVisita;
}