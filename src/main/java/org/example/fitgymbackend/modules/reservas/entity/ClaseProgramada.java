package org.example.fitgymbackend.modules.reservas.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.fitgymbackend.entity.Usuario;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "clase_programada")
public class ClaseProgramada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "catalogo_clase_id", nullable = false)
    private CatalogoClase catalogoClase;

    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Usuario instructor;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private Integer cupoMaximo;
    private Integer reservasActuales = 0;
    
    // PROGRAMADA, EN_CURSO, FINALIZADA, CANCELADA
    private String estado = "PROGRAMADA";

}
