package org.example.fitgymbackend.modules.inventario.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "inv_categoria_equipo")
public class CategoriaEquipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre; // CARDIO, PESAS, FUNCIONAL, ACCESORIOS

    private String descripcion;
}
