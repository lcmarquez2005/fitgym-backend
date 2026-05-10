package org.example.fitgymbackend.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    // CAPTACION, CONTACTO, VISITA, SEGUIMIENTO, CERRADO, PERDIDO
    @Column(nullable = false, length = 30)
    private String etapa = "CAPTACION";

    // INSTAGRAM, FACEBOOK, GOOGLE, REFERIDO, WALK_IN, OTRO
    @Column(length = 50)
    private String fuente;

    @Column(length = 500)
    private String notas;

    @Column(length = 150)
    private String asignadoA;

    @Column
    private LocalDate fechaVisita;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column
    private LocalDateTime fechaUltimoContacto;

    @Column
    private LocalDateTime fechaConversion;

    // Si se convirtió, guardamos el id del socio creado
    @Column
    private Long socioId;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}