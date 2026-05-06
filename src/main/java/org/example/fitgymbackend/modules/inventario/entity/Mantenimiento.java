package org.example.fitgymbackend.modules.inventario.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "inv_mantenimiento")
public class Mantenimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("equipo-mantenimientos")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @Column(nullable = false)
    private LocalDate fecha;

    // PREVENTIVO / CORRECTIVO
    private String tipo;
    private String descripcion;

    @Column(precision = 10, scale = 2)
    private BigDecimal costo;

    private String tecnico;   // nombre del técnico externo o interno

    // ID de la transacción de egreso generada automáticamente en Finanzas
    private Long transaccionId;
}
