package org.example.fitgymbackend.modules.reservas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "catalogo_clase")
public class CatalogoClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String categoria;
    private String nivel;
    private Integer duracionMinutos;
    private String materialNecesario;
    private Boolean activo = true;
    private java.math.BigDecimal costoExtra = java.math.BigDecimal.ZERO;

}
