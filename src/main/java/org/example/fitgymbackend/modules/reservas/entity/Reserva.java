package org.example.fitgymbackend.modules.reservas.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.fitgymbackend.entity.Socio;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clase_programada_id", nullable = false)
    private ClaseProgramada claseProgramada;

    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false)
    private Socio socio;

    private LocalDateTime fechaReserva;

    // CONFIRMADA, EN_WAITLIST, CANCELADA, ASISTIO, NO_ASISTIO
    private String estado;
    
    private Long transaccionId;

}
