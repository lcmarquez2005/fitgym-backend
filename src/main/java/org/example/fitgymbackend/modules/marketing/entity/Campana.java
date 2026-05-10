package org.example.fitgymbackend.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_campanas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Campana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    // BIENVENIDA, RETENCION, PRE_VENCIMIENTO, CUMPLEANIOS, RECUPERACION, EVENTO,
    // MANUAL
    @Column(nullable = false, length = 50)
    private String tipo;

    // BORRADOR, ACTIVA, PAUSADA, FINALIZADA
    @Column(nullable = false, length = 30)
    private String estado = "BORRADOR";

    @Column(columnDefinition = "TEXT")
    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String contenidoHtml;

    // Segmento al que va dirigida (puede ser null = todos)
    @Column
    private Long segmentoId;

    @Column
    private LocalDateTime fechaProgramada;

    @Column
    private LocalDateTime fechaEnvio;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(length = 150)
    private String creadoPor;

    // Métricas básicas
    @Column(nullable = false)
    private Integer totalEnviados = 0;

    @Column(nullable = false)
    private Integer totalAbiertos = 0;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}