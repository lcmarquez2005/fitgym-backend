package org.example.fitgymbackend.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_segmentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SegmentoMarketing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    // Criterios almacenados como JSON string simple
    // Ej: {"tipoMembresia":"PREMIUM","estatusSocio":"ACTIVO","diasSinAsistir":14}
    @Column(columnDefinition = "TEXT")
    private String criteriosJson;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column
    private LocalDateTime ultimaEjecucion;

    @Column
    private Integer totalSociosMatch;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}