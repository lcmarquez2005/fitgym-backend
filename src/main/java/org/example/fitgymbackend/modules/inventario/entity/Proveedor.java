package org.example.fitgymbackend.modules.inventario.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "inv_proveedor")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 20)
    private String rfc;       // Para vincular con DIOT

    private String contacto;  // Nombre del representante
    private String telefono;
    private String email;

    // NACIONAL / EXTRANJERO — alineado con los tipos del módulo de impuestos (DIOT)
    private String tipoProveedor = "NACIONAL";

    private Boolean activo = true;
}
