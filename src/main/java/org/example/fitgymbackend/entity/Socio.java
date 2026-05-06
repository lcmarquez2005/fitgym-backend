package org.example.fitgymbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "socios")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private String idSocio;
    private LocalDate fechaRegistro;
    private String estatus;
    private String tipoMembresia;
    private String descuento;
    private String costoMensual;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String lesiones;
    private String alergias;
    private String extras;
    private String foto;
}