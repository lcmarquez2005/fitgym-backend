package org.example.fitgymbackend.modules.reservas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "salon")
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer capacidad;
    private String equipamiento;
    private Boolean activo = true;

}
