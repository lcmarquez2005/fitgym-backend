package org.example.fitgymbackend.modules.inventario.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "inv_equipo")
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String marca;
    private String modelo;

    @Column(unique = true)
    private String numeroDeSerie;

    private LocalDate fechaCompra;
    private Integer vidaUtilAnios;          // para calcular depreciación
    private BigDecimal valorCompra;

    // OPERATIVO / MANTENIMIENTO / FUERA_DE_SERVICIO
    @Column(nullable = false)
    private String estado = "OPERATIVO";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private CategoriaEquipo categoria;

    @JsonManagedReference("equipo-mantenimientos")
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mantenimiento> mantenimientos = new ArrayList<>();
}
