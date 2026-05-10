package org.example.fitgymbackend.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_envios_campana")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class EnvioCampana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campana_id", nullable = false)
    private Campana campana;

    // Puede ser socio o lead
    @Column(length = 150)
    private String emailDestinatario;

    @Column(length = 150)
    private String nombreDestinatario;

    // SOCIO, LEAD
    @Column(length = 20)
    private String tipoDestinatario;

    @Column
    private Long destinatarioId;

    // ENVIADO, FALLIDO
    @Column(nullable = false, length = 20)
    private String estado = "ENVIADO";

    @Column
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column
    private Boolean abierto = false;

    @Column
    private LocalDateTime fechaApertura;

    @PrePersist
    protected void onCreate() {
        fechaEnvio = LocalDateTime.now();
    }
}