// SeguimientoLeadRequest.java
package org.example.fitgymbackend.modules.marketing.dto;

import lombok.Data;

@Data
public class SeguimientoLeadRequest {
    private String tipoContacto;
    private String descripcion;
    private String realizadoPor;
}