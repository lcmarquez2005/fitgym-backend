package org.example.fitgymbackend.model.request;

import lombok.Data;

@Data
public class SocioRequest {
    private String nombreCompleto;
    private String telefono;
    private String email;
    private String fechaNacimiento;
    private String sexo;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private String idSocio;
    private String fechaRegistro;
    private String estatus;
    private String tipoMembresia;
    private String descuento;
    private String costoMensual;
    private String fechaInicio;
    private String fechaFin;
    private String lesiones;
    private String alergias;
    private String extras;
    private String foto;
}