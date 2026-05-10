package org.example.fitgymbackend.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_promociones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromocionDescuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    // PORCENTAJE, MONTO_FIJO
    @Column(nullable = false, length = 20)
    private String tipoDescuento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column
    private LocalDate fechaInicio;

    @Column
    private LocalDate fechaFin;

    @Column
    private Integer limiteUsosTotales;

    @Column(nullable = false)
    private Integer usosActuales = 0;

    @Column
    private Integer usosPorPersona = 1;

    // NUEVO, RENOVACION, AMBOS
    @Column(length = 20)
    private String aplicaA = "AMBOS";

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (codigo != null)
            codigo = codigo.toUpperCase().trim();
    }
}