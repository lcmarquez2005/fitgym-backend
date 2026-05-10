package org.example.fitgymbackend.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_seguimiento_leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    // LLAMADA, EMAIL, WHATSAPP, VISITA, NOTA
    @Column(nullable = false, length = 30)
    private String tipoContacto;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(length = 150)
    private String realizadoPor;

    @Column(nullable = false)
    private LocalDateTime fechaContacto = LocalDateTime.now();

    // Etapa del lead al momento del contacto
    @Column(length = 30)
    private String etapaEnContacto;

    @PrePersist
    protected void onCreate() {
        fechaContacto = LocalDateTime.now();
    }
}