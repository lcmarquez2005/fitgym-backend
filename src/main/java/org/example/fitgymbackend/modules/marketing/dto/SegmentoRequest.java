// SegmentoRequest.java
package org.example.fitgymbackend.modules.marketing.dto;

import lombok.Data;

@Data
public class SegmentoRequest {
    private String nombre;
    private String descripcion;
    private String criteriosJson;
}