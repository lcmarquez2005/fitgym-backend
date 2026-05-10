// CampanaRequest.java
package org.example.fitgymbackend.modules.marketing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CampanaRequest {
    private String nombre;
    private String descripcion;
    private String tipo;
    private String asunto;
    private String contenidoHtml;
    private Long segmentoId;
    private LocalDateTime fechaProgramada;
    private String creadoPor;
}