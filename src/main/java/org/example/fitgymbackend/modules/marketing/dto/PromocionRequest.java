// PromocionRequest.java
package org.example.fitgymbackend.modules.marketing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PromocionRequest {
    private String nombre;
    private String codigo;
    private String tipoDescuento;
    private BigDecimal valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer limiteUsosTotales;
    private Integer usosPorPersona;
    private String aplicaA;
    private String descripcion;
}