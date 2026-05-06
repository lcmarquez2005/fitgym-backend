package org.example.fitgymbackend.model.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SocioResponse {
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